import boto3
from typing import List, Dict, Any
from token_compressor import Compressor  # Assumes you already implemented LLM compression logic here
import datetime

class CustomMemoryManager:
    """
    Manages conversation memory for UnCypher.
    Compresses old messages using a small LLM, preserves recent tool output and critical code logic separately.
    Stores raw data in DynamoDB for frontend replay.
    """

    def __init__(self, user_id: str, table_name: str):
        self.user_id = user_id
        self.dynamo = boto3.resource("dynamodb")
        self.table = self.dynamo.Table(table_name)
        self.compressor = Compressor()

    def log_message(self, role: str, content: str, metadata: Dict[str, Any] = None):
        timestamp = datetime.datetime.utcnow().isoformat()
        item = {
            "user_id": self.user_id,
            "timestamp": timestamp,
            "role": role,
            "content": content,
            "metadata": metadata or {},
        }
        self.table.put_item(Item=item)

    def get_recent_context(self, limit: int = 64) -> List[Dict[str, Any]]:
        response = self.table.query(
            KeyConditionExpression="user_id = :uid",
            ExpressionAttributeValues={":uid": self.user_id},
            ScanIndexForward=False,  # descending
            Limit=limit
        )
        return list(reversed(response.get("Items", [])))  # order oldest to newest

    def build_context_for_orchestrator(self) -> str:
        messages = self.get_recent_context(limit=64)
        if len(messages) <= 8:
            return self._render(messages)

        early_msgs = messages[:-8]  # first N-8
        recent_msgs = messages[-8:]  # last 8

        # Compress earliest 4
        to_compress = early_msgs[:4]
        compressed_chunk = self.compressor.summarize_messages(to_compress)

        # Directly include last 4 messages without compression
        recent_direct = recent_msgs[-4:]

        # Embed both
        final = []
        final.append(f"[COMPRESSED CONTEXT]: {compressed_chunk}\n")
        final.extend([f"[{msg['role'].upper()}]: {msg['content']}" for msg in recent_direct])

        return "\n".join(final)

    def _render(self, messages: List[Dict[str, Any]]) -> str:
        return "\n".join(
            f"[{msg['role'].upper()}]: {msg['content']}" for msg in messages
        )

    # === Code-Specific Handling ===

    def store_code_snippet(self, code_id: str, code_logic: str, tool_type: str):
        self.table.put_item(Item={
            "user_id": self.user_id,
            "timestamp": f"CODE#{code_id}",
            "role": "code",
            "tool_type": tool_type,
            "content": code_logic,
            "metadata": {"is_code": True}
        })

    def get_code_snippets(self) -> List[Dict[str, str]]:
        response = self.table.query(
            KeyConditionExpression="user_id = :uid",
            ExpressionAttributeValues={":uid": self.user_id},
        )
        return [item for item in response.get("Items", []) if item.get("role") == "code"]
