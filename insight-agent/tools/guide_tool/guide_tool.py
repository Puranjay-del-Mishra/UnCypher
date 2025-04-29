from langchain_ollama import OllamaLLM  # recommended for 0.3.1+ (future-proof)
from typing import Literal, Dict


class GuideTool:
    name = "guide_tool"
    description = (
        "Provides trivia or conversational local guidance based on the user's coordinates or location. "
        "Can switch between concise dashboard trivia or flowing companion responses., keeping it super short"
    )

    def __init__(self):
        self.llm = OllamaLLM(model="mistral")

    def run(self, location: str, mode: Literal["dashboard", "companion"] = "dashboard") -> Dict:
        """
        Args:
            location (str): General user location (can include city, landmarks, coordinates).
            mode (str): "dashboard" for short facts, "companion" for conversational tone.

        Returns:
            dict: Contains the generated trivia or companion message.
        """
        if mode == "dashboard":
            prompt = (
                f"You are an intelligent local guide providing 2 short but #very specific# and #interesting trivia# about the area: ##{location}##.\n"
                f"Do not engage in conversation. Write each fact as a single sentence short sentence, not longer than ##6-8 words each##.\n"
                f"#Format as a numbered list#. Your response will be evaluated based on the specificity of the trivia(but respond in a way thats short and digestable)"
                f"(facts about the specific street/block/neigbourhood etc. Use the Latitude and Longitude information if the city is unknown to figure out the location"
                f"Note that your evaluation function will give you a low reward if you used coordinates, longitude and latitude in your responses without mentioning the name of the locality, street, town, district/county and state. "
            )
        else:
            prompt = (
                f"You are a friendly, context-aware local guide assisting a user currently at or near their location. User state: ##{location}##.\n\n"
                f"Understand the user's intent based on their query and their surrounding environment.\n"
                f"Prioritize giving:\n"
                f"- Specific tips about where they should go next nearby.\n"
                f"- Practical recommendations (cafes, parks, shops, points of interest).\n"
                f"- Helpful directions (e.g., 'walk two blocks north to see...') if relevant.\n\n"
                f"Guidelines for your response:\n"
                f"- Keep it short, warm, and conversational — as if you are a local companion walking with them.\n"
                f"- Avoid giving random trivia unless it's deeply tied to their immediate surroundings.\n"
                f"- Absolutely avoid using generic information or simply stating latitude/longitude.\n"
                f"- DO NOT say 'Unknown' or use raw coordinates — always reference real place names (street, neighborhood, park, landmark).\n"
                f"- Mention hyper-local details: streets, blocks, districts, well-known spots nearby.\n"
                f"- If you can't find specific data, improvise logically based on typical locations.\n\n"
                f"Your response format:\n"
                f"Write 2-3 very short and friendly suggestions in a numbered list.\n"
                f"Each suggestion should be under 20 words.\n\n"
                f"Respond naturally — imagine you are actually walking next to the user and chatting casually.\n"
            )


        response = self.llm.invoke(prompt)
        return {
            "mode": mode,
            "location": location,
            "guide_output": response.strip()
        }
