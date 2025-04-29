from fastapi import FastAPI, Request
from fastapi.responses import StreamingResponse
from models.insight_request import InsightRequest, PassiveInsightRequest
from models.insight_response import InsightResponse, PassiveInsightResponse
from services.insight_service import InsightService
import time

app = FastAPI(title="UnCypher Insight Agent")

insight_service = InsightService()

@app.post("/infer", response_model=InsightResponse)
async def infer(request: InsightRequest):
    if request.stream:
        async def stream_response():
            async for chunk in insight_service.handle_streaming_insight(request):
                yield chunk
        return StreamingResponse(stream_response(), media_type="text/plain")
    else:
        start_time = time.time()
        response = insight_service.handle_insight(request)
        duration = round((time.time() - start_time) * 1000)
        print(f"📡 /infer completed in {duration} ms")
        return response

@app.post("/infer-passive", response_model=PassiveInsightResponse)
def infer_passive(request: PassiveInsightRequest):
    start_time = time.time()
    response = insight_service.handle_passive_insight(request)
    dur = round((time.time()-start_time) * 1000)
    print(f"📡 /infer-passive completed in {dur} ms")
    return response

from utils.token_manager import TokenManager

try:
    token_manager = TokenManager()
except Exception as e:
    print(f"⛔ Failed to initialize TokenManager!: {e}")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=5000, reload=True)

