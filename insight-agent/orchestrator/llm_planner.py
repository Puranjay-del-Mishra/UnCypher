from langchain_ollama import OllamaLLM  # recommended for 0.3.1+ (future-proof)
from typing import AsyncGenerator

class HermesLLMPlanner:
    def __init__(self, streaming: bool = False):
        self.streaming_enabled = streaming
        self.llm = OllamaLLM(model="nous-hermes", streaming=streaming)

    def generate(self, prompt: str) -> str:
        return self.llm.invoke(prompt)

    async def stream(self, prompt: str) -> AsyncGenerator[str, None]:
        for chunk in self.llm.stream(prompt):
            yield chunk.content


class Planner:
    def __init__(self, strategy=None):
        self.strategy = strategy or HermesLLMPlanner()

    def get_next_action(self, prompt: str) -> str:
        return self.strategy.generate(prompt)

    async def stream(self, prompt: str) -> AsyncGenerator[str, None]:
        async for token in self.strategy.stream(prompt):
            yield token


class MistralPlanner:
    def __init__(self, streaming: bool = False):
        self.streaming_enabled = streaming
        self.llm = OllamaLLM(model="mistral", streaming=streaming)

    def generate(self, prompt: str) -> str:
        return self.llm.invoke(prompt)

    async def stream(self, prompt: str) -> AsyncGenerator[str, None]:
        for chunk in self.llm.stream(prompt):
            yield chunk.content


