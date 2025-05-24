from langchain_ollama import OllamaLLM  # recommended for 0.3.1+ (future-proof)
from typing import AsyncGenerator
import torch
from transformers import AutoTokenizer, AutoModelForCausalLM

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
        self.strategy = strategy or HFLlama3Planner()  # 🧠 Set LLaMA 3 as the new default

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


class HFLlama3Planner:
    def __init__(self, model_name: str = "meta-llama/Meta-Llama-3-8B-Instruct", streaming: bool = False):
        self.streaming_enabled = streaming
        self.tokenizer = AutoTokenizer.from_pretrained(model_name)
        self.model = AutoModelForCausalLM.from_pretrained(
            model_name,
            torch_dtype=torch.bfloat16,  # You can also use float16 if your GPU prefers it
            device_map="auto",
            attn_implementation="flash_attention_2",  # Uses FlashAttention if installed
        )
        self.model.eval()

    def generate(self, prompt: str) -> str:
        inputs = self.tokenizer(prompt, return_tensors="pt").to(self.model.device)
        with torch.no_grad():
            outputs = self.model.generate(
                **inputs,
                max_new_tokens=512,
                do_sample=True,
                top_p=0.95,
                temperature=0.7,
            )
        return self.tokenizer.decode(outputs[0], skip_special_tokens=True)

    async def stream(self, prompt: str) -> AsyncGenerator[str, None]:
        output = self.generate(prompt)
        for token in output.split():
            yield token + " "