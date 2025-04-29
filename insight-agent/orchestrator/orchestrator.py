import json
from typing import Dict, Any, List, AsyncGenerator
from memory_manager import Memory
from .llm_planner import Planner, MistralPlanner
from toolset import ToolRegistry
from orchestrator.plan_step import PlanStep
import time

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
        self.planner = Planner(strategy=MistralPlanner(streaming=True))
        self.loop_trace = []

    def run(self, payload: Dict[str, Any]) -> Dict[str, Any]:
        context = self.memory.build_context_for_orchestrator()
        tool_descriptions = self.tool_registry.describe_tools()
        plan_prompt = self._build_prompt(payload, context, tool_descriptions)

        planner_start = time.time()
        planner_output = self.planner.get_next_action(plan_prompt)
        print("🧠 Planner output:", planner_output)
        print(f"🧠 Planner LLM call took {round((time.time() - planner_start) * 1000)} ms")

        plan_steps = self._parse_plan(planner_output)

        result_map = {}

        for idx, step in enumerate(plan_steps):
            tool = self.tool_registry.get_tool(step.tool_name)
            if not tool:
                raise ValueError(f"Tool '{step.tool_name}' not found.")

            # Resolve input dynamically
            if step.input_id.startswith("out_"):
                input_data = result_map.get(step.input_id)
            elif "+" in step.input_id:
                keys = step.input_id.split("+")
                input_data = "\n".join([str(result_map.get(k.strip(), "")) for k in keys])
            else:
                input_data = str(payload)

            tool_start = time.time()
            tool_result = tool.run(input_data)
            print(f"🔧 Tool '{step.tool_name}' executed in {round((time.time() - tool_start) * 1000)} ms")
            print(f"🔵 Tool input: {input_data}")

            result_key = f"out_{idx + 1}"
            result_map[result_key] = tool_result

        # Save memory + trace
            self.memory.log_message(step.tool_name, str(tool_result))
            self.loop_trace.append({
            "tool": step.tool_name,
            "input": input_data,
            "reason": step.reason,
            "output": tool_result
        })

    # 🧠 Real fix: properly get the final tool output
        final_result = result_map.get(f"out_{len(plan_steps)}", None)

        if isinstance(final_result, dict):
            # If the final tool returned a dict, try extracting key fields
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

    def _build_prompt(self, payload: Dict[str, Any], context: str, tool_descriptions: str) -> str:
        query = payload.get("query", "")
        location = payload.get("location")
        sensors = payload.get("sensors")

        # Optional dynamic context building if location/sensors exist
        extra_context_parts = []
        if location:
            extra_context_parts.append(f"User location: {location}.")
        if sensors:
            extra_context_parts.append("Sensor data provided.")

        dynamic_context = " ".join(extra_context_parts)
        print('Dynamic context is: ', dynamic_context)
        return (
            "You are an AI orchestrator that generates JSON outputs for the backend.\n"
            "Your job is to create a minimal pipeline of tools and chain their outputs to solve the user's request.\n\n"
            "🔧 Available tools:\n"
            f"{tool_descriptions}\n\n"
            "🔁 Respond in JSON array format only. Each object must have:\n"
            "- TOOL_NAME (string): one of the tools listed above\n"
            "- INPUT_ID (string): either 'inp_1', or a previous step like 'out_1', or combined like 'out_1+out_2'\n"
            "- REASON (string): explain briefly why this tool is needed next\n\n"
            "DO NOT write explanations. DO NOT return anything outside the JSON. Avoid triggering the same tool in a row, compress them into one single tool call.\n"
            "Only use tools from the provided list.\n"
            "Always finish with ##DONE## after the array.\n\n"
            "Use the below examples to figure out the structure of your output and how you should approach a query. Keep the plan minimilastic\n"
            "🧪 Example 1 (chained inputs):\n"
            "[\n"
            "  {\"TOOL_NAME\": \"guide_tool\", \"INPUT_ID\": \"inp_1\", \"REASON\": \"Get trivia near user.\"},\n"
            "  {\"TOOL_NAME\": \"aesthetic_tool\", \"INPUT_ID\": \"inp_1\", \"REASON\": \"Score streets visually.\"},\n"
            "  {\"TOOL_NAME\": \"route_tool\", \"INPUT_ID\": \"out_1+out_2\", \"REASON\": \"Blend both for scenic route.\"}\n"
            "]\n\n"
            "🧪 Example 2 (recursive refinement):\n"
            "[\n"
            "  {\"TOOL_NAME\": \"route_tool\", \"INPUT_ID\": \"inp_1\", \"REASON\": \"Initial route and refine the path.\"},\n"
            "  {\"TOOL_NAME\": \"verifier_tool\", \"INPUT_ID\": \"out_2\", \"REASON\": \"Check user fit.\"}\n"
            "]\n\n"
            "If no tool exists to carry out the query, default to: guide_tool"
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
