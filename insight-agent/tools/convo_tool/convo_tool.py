from langchain_ollama import OllamaLLM  # Assuming you're using Ollama-backed models
from typing import Dict

class ConvoTool:
    name = "convo_tool"
    description = (
        "Handles general conversational queries from the user that are not strictly navigational. "
        "Provides friendly, bright, and simple responses based on the user's query alone."
    )

    def __init__(self):
        self.llm = OllamaLLM(model="mistral")  # or whatever model you want

    def run(self, query: str) -> Dict:
        """
        Args:
            query (str): The user's raw query.

        Returns:
            dict: The conversational response.
        """
        prompt = (
            f"You are a friendly, engaging AI companion.\n\n"
            f"Your job is to answer the following user question in a very bright, simple, and positive tone:\n\n"
            f"User Question: ##{query}##\n\n"
            f"Guidelines:\n"
            f"- Keep the answer under 3-4 short sentences.\n"
            f"- Avoid technical jargon unless explicitly asked.\n"
            f"- Make the answer feel warm, lively, and human-like.\n"
            f"- If the question is unclear, assume positive intent and gently respond anyway.\n"
            f"- Never say 'I am an AI model', stay personal and friendly.\n\n"
            f"Respond now with a clean, natural paragraph:\n"
        )

        response = self.llm.invoke(prompt)

        return {
            "convo_output": response.strip()
        }

