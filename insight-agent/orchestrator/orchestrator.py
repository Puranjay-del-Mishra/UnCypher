import json
import time
import threading
import requests
from typing import Dict, Any, List, AsyncGenerator

from memory_manager import Memory
from .llm_planner import Planner, HFLlama3Planner

from toolset import ToolRegistry
from orchestrator.plan_step import PlanStep


class Orchestrator:
    description = (
        "The Orchestrator is UnCypher's core AI loop. It manages planning, "
        "tool execution, memory access, and trace tracking using an LLM "
        "to make dynamic routing decisions based on user state and session state."
    )
    name = "UnCypher Core Orchestrator"

    def __init__(self, user_id: str, table_name: str):
        self.user_id = user_id
        self.memory = Memory(user_id, table_name)
        self.tool_registry = ToolRegistry()
        self.planner = Planner(strategy=HFLlama3Planner())
        self.loop_trace = []

    def run(self, payload: Dict[str, Any]) -> Dict[str, Any]:
        context = self.memory.build_context_for_orchestrator()
        tool_descriptions = self.tool_registry.describe_tools()

        # 🔧 Build location string from context
        raw_location = payload.get("location", "Unknown, Unknown")
        context_data = payload.get("context", {})

        if isinstance(context_data, str):
            try:
                context_data = json.loads(context_data.replace("'", "\""))
            except Exception as e:
                print("[WARN] Failed to parse context_data:", e)
                context_data = {}

        if context_data.get("lat") and context_data.get("lng"):
            location_string = f"{context_data.get('lat')},{context_data.get('lng')}"
        else:
            location_string = raw_location

        # 🔧 Build planner prompt and run planner
        plan_prompt = self._build_prompt(payload, context, tool_descriptions)
        planner_start = time.time()
        planner_output = self.planner.generate(plan_prompt)
        print("🧠 Planner output:", planner_output)
        print(f"🧠 Planner LLM call took {round((time.time() - planner_start) * 1000)} ms")

        plan_steps = self._parse_plan(planner_output)
        result_map = {}

        for idx, step in enumerate(plan_steps):
            tool = self.tool_registry.get_tool(step.tool_name)
            if not tool:
                raise ValueError(f"Tool '{step.tool_name}' not found.")

            # 🔁 Custom input routing per tool
            if step.tool_name == "navigation_tool":
                prev_result = result_map.get(step.input_id)
                if isinstance(prev_result, dict) and "poi_instructions" in prev_result:
                    input_data = {
                    "mode": "batch",
                    "instructions": prev_result["poi_instructions"],
                    "locality": location_string
                    }
                else:
                    query = payload.get("query", "").strip()
                    input_data = f"route\n{query}\n{location_string}"

            elif step.tool_name == "poi_refiner_tool":
                input_data = {
                "query": payload.get("query", ""),
                "context": context_data
                }

            elif "+" in step.input_id:
                keys = step.input_id.split("+")
                input_data = "\n".join([str(result_map.get(k.strip(), "")) for k in keys])

            elif step.input_id.startswith("out_"):
                input_data = result_map.get(step.input_id)

            else:
                input_data = payload.get("query", "")


    # ✅ Execute tool
            tool_start = time.time()
            tool_result = tool.run(input_data)
            print(f"🔧 Tool '{step.tool_name}' executed in {round((time.time() - tool_start) * 1000)} ms")
            print(f"🔵 Tool input: {input_data}")

            result_key = f"out_{idx + 1}"
            result_map[result_key] = tool_result

            # 💾 Save memory + trace
            self.memory.log_message(step.tool_name, str(tool_result))
            self.loop_trace.append({
                "tool": step.tool_name,
                "input": input_data,
                "reason": step.reason,
                "output": tool_result
            })

            # 📡 Dispatch map commands if present
            if isinstance(tool_result, dict) and tool_result.get("map_commands"):
                self.dispatch_map_commands(self.user_id, tool_result.get("map_commands"))

        # 🧾 Final output resolution
        final_result = result_map.get(f"out_{len(plan_steps)}", None)
        if isinstance(final_result, dict):
            final_output = (
                    final_result.get("guide_output") or
                    final_result.get("convo_output") or
                    str(final_result)
            )
        else:
            final_output = str(final_result) if final_result else "No insights generated."

        return {
            "trace": self.loop_trace,
            "final_state": context,
            "summary": final_output
        }


    async def stream(self, user_input: str) -> AsyncGenerator[str, None]:
        context = self.memory.build_context_for_orchestrator()
        tool_descriptions = self.tool_registry.describe_tools()
        plan_prompt = self._build_prompt(user_input, context, tool_descriptions)

        async for token in self.planner.stream(plan_prompt):
            yield token

    def dispatch_map_commands(self, user_id, map_commands):
        """
        Fire-and-forget HTTP POST to backend map command route
        """
        def _dispatch():
            try:
                print("🚀 Dispatching Map Commands to backend...")
                response = requests.post(
                    "http://localhost:8080/api/insights/mapcommand",
                    json={
                        "userId": user_id,
                        "commands": map_commands
                    },
                    timeout=3
                )
                response.raise_for_status()
                print("✅ Map commands sent successfully.")
            except Exception as e:
                print(f"❌ Failed to dispatch map commands: {e}")

        threading.Thread(target=_dispatch).start()

    def _build_prompt(self, payload: Dict[str, Any], context: str, tool_descriptions: str) -> str:
        query = payload.get("query", "")
        location = payload.get("location")
        sensors = payload.get("sensors")

        extra_context_parts = []
        if location:
            extra_context_parts.append(f"User location: {location}.")
        if sensors:
            extra_context_parts.append("Sensor data provided.")

        dynamic_context = " ".join(extra_context_parts)
        print('Dynamic context is: ', dynamic_context)
        print('Tool Descriptions: ', tool_descriptions)
        return (
            "You are an AI orchestrator that generates a minimal pipeline of tool selection which is used to solve user's needs.\n"
            "Your job is to chose the set of tools from the tool registry in the correct order and chain their outputs to solve the user's request.\n\n"
            "🔧 Available tools:\n"
            f"{tool_descriptions}\n\n"
            "🔁 Respond in JSON array format only, avoid assuming tools that are not defined in the tool registry, described above. Each object must have:\n"
            "- TOOL_NAME (string): one of the tools listed above\n"
            "- INPUT_ID (string): either 'inp_1', or a previous step like 'out_1', or combined like 'out_1+out_2'\n"
            "- REASON (string): explain briefly why this tool is needed next\n\n"
            "DO NOT write explanations. DO NOT return anything outside the JSON. Avoid triggering the same tool in a row, compress them into one single tool call.\n"
            "Only use tools from the provided list.\n"
            "Always finish with ##DONE## after the array.\n\n"
            "##Use the below examples and ##Strictly## structure your output like below and understand how you should approach a query. Keep the plan minimilastic##\n"
            "🧪 Example 1 (chained inputs):\n"
            "[\n"
            "  {\"TOOL_NAME\": \"guide_tool\", \"INPUT_ID\": \"inp_1\", \"REASON\": \"Get trivia near user.\"},\n"
            "  {\"TOOL_NAME\": \"aesthetic_tool\", \"INPUT_ID\": \"inp_1\", \"REASON\": \"Score streets visually.\"},\n"
            "  {\"TOOL_NAME\": \"navigation_tool\", \"INPUT_ID\": \"out_1+out_2\", \"REASON\": \"Blend both for scenic route.\"}\n"
            "]\n\n"
            "🧪 Example 2 (recursive refinement):\n"
            "[\n"
            "  {\"TOOL_NAME\": \"poi_refiner_tool\", \"INPUT_ID\": \"inp_1\", \"REASON\": \"Refine user query for setting routes and markers.\"},\n"
            "  {\"TOOL_NAME\": \"navigation_tool\", \"INPUT_ID\": \"out_1\", \"REASON\": \"Process the POI instructions\"}\n"
            "  {\"TOOL_NAME\": \"guide_tool\", \"INPUT_ID\": \"inp_1\", \"REASON\": \"Generate conversational response for the user destination, route and current location\"}\n"
            "]\n\n"
            f"User Query: \"{query}\"\n"
            f"Context: {context} {dynamic_context}\n\n"
            "Now return ONLY the JSON plan. Then write ##DONE## on the next line."
        )

    def _parse_plan(self, output: str) -> List[PlanStep]:
        if "##DONE##" in output:
            output = output.replace("##DONE##", "")
        try:
            raw_plan = json.loads(output.strip())
            return [PlanStep(**step) for step in raw_plan if step.get("TOOL_NAME")]
        except Exception as e:
            raise ValueError(f"Failed to parse planner output: {e}\nOutput was:\n{output}")
