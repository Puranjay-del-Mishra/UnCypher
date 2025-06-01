from langchain_ollama import OllamaLLM  # recommended for 0.3.1+ (future-proof)
from typing import AsyncGenerator
import torch
from transformers import AutoTokenizer, AutoModelForCausalLM, BitsAndBytesConfig
import os
import openai
from openai import OpenAI

from dotenv import load_dotenv

load_dotenv()
api_key=os.getenv("OPENROUTER_API_KEY")
api_base = "https://openrouter.ai/api/v1"

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
        self.strategy = strategy or OpenRouterLlama3Planner()  # 🧠 Set LLaMA 3 as the new default

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


class OpenRouterLlama3Planner:
    def __init__(self, model_name="meta-llama/llama-3.3-8b-instruct:free", streaming: bool = False):
        self.model_name = model_name
        self.streaming_enabled = streaming
        self.client = OpenAI(api_key=api_key, base_url=api_base)

    def generate(self, prompt: str) -> str:
        print("prompt for LLMplanner: ", prompt)
        response = self.client.chat.completions.create(
            messages=[
                {"role": "system", "content": "You are the LLM planner for an AI engine, which follows instructions of the user prompt"},
                {"role": "user", "content": prompt},
            ],
            model=self.model_name,
            temperature=0.7,
            max_tokens=1024,
            top_p=0.95,
        )
        print(response)
        return response.choices[0].message.content

    async def stream(self, prompt: str) -> AsyncGenerator[str, None]:
        response = self.client.chat.completions.create(
            messages=[
                {"role": "system", "content": "You are the LLM planner for an AI engine, which follows instructions of the user prompt"},
                {"role": "user", "content": prompt},
            ],
            model=self.model_name,
            temperature=0.7,
            max_tokens=1024,
            top_p=0.95,
            stream=True,
        )

        for chunk in response:
            content = chunk.choices[0].delta.get("content", "")
            yield content