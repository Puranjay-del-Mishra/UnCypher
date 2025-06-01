import time
from orchestrator import Orchestrator
from tools.guide_tool import GuideTool
from models.insight_request import InsightRequest, PassiveInsightRequest
from models.insight_response import InsightResponse, PassiveInsightResponse
import requests
from utils.token_manager import token_manager
import os
import datetime
from utils.poi_feeder import POIFeeder

class InsightService:
    def __init__(self):
        self.guide_tool = GuideTool()

    def handle_passive_insight(self, request: PassiveInsightRequest) -> PassiveInsightResponse:
        import time
        start = time.time()

        location = request.location
        lat, lng = location.split(",")

        # POI Feeder knows how to use NavigationTool internally
        poi_feeder = POIFeeder()
        fake_user_state = type("UserState", (), {"location": (lat, lng)})  # temp stand-in

        poi_snippets = poi_feeder.get_nearby_poi_snippets(
            user_state=fake_user_state
        )

        poi_context = "\n".join(f"- {p}" for p in poi_snippets if p.strip())
        print('POI context for passive insights: ', poi_context)
        full_prompt = f"User is near ({lat}, {lng}). Here are some POIs nearby:\n{poi_context}\n\nGenerate engaging trivia for a dashboard display."

        trivia_output = self.guide_tool.run(full_prompt, mode="dashboard")

        insights_raw = trivia_output.get("guide_output", [])
        if isinstance(insights_raw, str):
            insights = [insights_raw]
        elif isinstance(insights_raw, list):
            insights = insights_raw
        else:
            insights = ["[Invalid guide_tool output]"]

        duration = round((time.time() - start) * 1000)
        print(f"📊 Passive insight (POI + guide_tool) completed in {duration} ms")

        return PassiveInsightResponse(
            insights=insights,
            toolUsed="guide_tool+poi_feeder",
            meta={
                "location": location,
                "deviceType": request.deviceType or "unknown",
                "source": "dashboard-passive",
                "duration_ms": duration,
                "poi_count": len(poi_snippets)
            }
        )

    def save_chat_message(self, user_id: str, role: str, content: str):
        token = token_manager.get_token()  # ✅ safely fetch from memory

        if not token:
            print("⛔ No agent token available!")
            return

        payload = {
            "userId": user_id,
            "role": role,              # NOT 'sender'
            "content": content,        # NOT 'text'
            "timestamp": datetime.datetime.utcnow().isoformat() + "Z"  # Add timestamp
        }

        print('SAVE CHAT PAYLOAD: ', payload)
        try:
            response = requests.post(
                "http://localhost:8080/api/insights/chat/save",
                headers={
                    "Authorization": f"Bearer {token}",
                    "Content-Type": "application/json"
                },
                json=payload,
                timeout=5
            )
            response.raise_for_status()
            print(f"✅ Chat saved successfully for user: {user_id}")
        except Exception as e:
            print(f"❌ Failed to save chat to backend Redis: {e}")


    def handle_insight(self, request: InsightRequest) -> InsightResponse:
        t0 = time.time()
        orchestrator = Orchestrator(user_id=request.userId, table_name="user")
        t1 = time.time()
        print("Regular insight received: ", request)
        result = orchestrator.run({
            'query': request.query,
            'location': request.location,
            'sensors': request.sensors
        })
        t2 = time.time()

        print(f"⏱️ Orchestrator init: {round((t1 - t0) * 1000)} ms")
        print(f"⏱️ Orchestrator run: {round((t2 - t1) * 1000)} ms")

        final_summary = result.get("summary", "No insights generated.")
        if isinstance(final_summary, dict):
            final_answer = final_summary.get("guide_output", "No answer found.")
        else:
            final_answer = final_summary

        # ✅ Correct save calls
        self.save_chat_message(request.userId, "user", request.query)
        self.save_chat_message(request.userId, "assistant", final_answer)

        return InsightResponse(
            answer=final_answer,
            toolUsed="orchestrator",
            meta={
                "trace": result.get("trace", []),
                "location": request.location,
                "context": request.context,
                "duration_ms": round((t2 - t0) * 1000)
            }
        )


    async def handle_streaming_insight(self, request: InsightRequest):
        orchestrator = Orchestrator(user_id=request.userId, table_name="user")
        async for chunk in orchestrator.stream(request.query):
            yield chunk

