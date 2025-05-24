from langchain_ollama import OllamaLLM
from typing import Dict, Any, List
import json

class PoiRefinerTool:
    name = "poi_refiner_tool"
    description = (
        "Point of Interest parser refines user input for the downstream task of navigation"
        "This tool makes the job of navigation easier by breaking down user request into actionable and sequential steps for the navigation tool. Identifies tasks like setting route, marker, and generating info. It uses LLM reasoning to understand unstructured user intent."
    )

    def __init__(self):
        self.model = OllamaLLM(model="mistral", temperature=0.3)

    def run(self, input_data: Dict[str, Any]) -> Dict[str, Any]:
        query = input_data.get("query", "")
        context = input_data.get("context", {})

        prompt = self.build_prompt(query, context)
        output = self.model.invoke(prompt)

        try:
            instructions = json.loads(output.strip().replace("##DONE##", ""))
            return {"poi_instructions": instructions}
        except Exception as e:
            return {
                "error": f"Failed to parse POI instructions: {str(e)}",
                "raw_output": output
            }

    def build_prompt(self, query: str, context: Dict[str, Any]) -> str:
        location = f"{context.get('lat')},{context.get('lng')}" if context.get("lat") and context.get("lng") else "unknown"
        memory = context.get("memory", "")

        return f"""
You are a location command parser. Your task is to extract clear, minimal POI instructions from unstructured user queries.

🧠 KEY RULES:
- Use "route" only if the user intends to move or navigate to a location.
- Use "marker" only if the user is (i) setting a route for navigation OR (ii) mentions a place worth highlighting and wants to explore more about it.
- If both apply (e.g., "get me to the pub and tell me something interesting"), include route, info, and marker.

Always output a JSON list of instruction objects with the following fields:
- action: one of ["route", "marker", "fly_to", "info"]
- destination: name of the place
- popup: a short summary string to show in a map popup
- color: a visual color like "yellow", "blue", "red", etc.

Do NOT include coordinates. Do NOT return anything outside the JSON block.

User query: "{query}"
User location: {location}
User memory: {memory}

---

EXAMPLE 1: Just a route

Query: "take me to Tim Hortons"
Output:
[
  {{
    "action": "route",
    "destination": "Tim Hortons",
    "popup": "Navigating to Tim Hortons",
    "color": "yellow"
  }},
  {{
    "action": "marker",
    "destination": "Tim Hortons",
    "popup": "Destination: Tim Hortons",
    "color": "blue"
  }}
]

---

EXAMPLE 2: Just a marker (no movement, but interested in location)

Query: "what's around the old post office?"
Output:
[
  {{
    "action": "marker",
    "destination": "Old Post Office",
    "popup": "Old Post Office Marker",
    "color": "red"
  }},
  {{
    "action": "info",
    "destination": "Old Post Office",
    "popup": "Details about the Old Post Office",
    "color": "purple"
  }}
]

---

EXAMPLE 3: Hybrid (route + info + marker)

Query: "get me to the pub and tell me something interesting about it"
Output:
[
  {{
    "action": "route",
    "destination": "The Local Pub",
    "popup": "Heading to The Local Pub",
    "color": "yellow"
  }},
  {{
    "action": "marker",
    "destination": "The Local Pub",
    "popup": "Pub Destination Marker",
    "color": "blue"
  }},
  {{
    "action": "info",
    "destination": "The Local Pub",
    "popup": "Trivia about The Local Pub",
    "color": "purple"
  }}
]

---

Only return the JSON list for the current query. End with ##DONE##
""".strip()



