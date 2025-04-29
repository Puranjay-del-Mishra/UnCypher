from typing import List, Dict
import re

class Compressor:
    """
    Compresses long conversation traces using tag-based semantic abstraction and summary fallback.
    Intended to keep token width minimal while preserving essential routing-relevant info for the Orchestrator.
    """

    def __init__(self):
        pass

    def summarize_messages(self, messages: List[Dict]) -> str:
        """
        Convert structured tool messages into a compact tag format.

        Example output:
        [SENTIMENT=stressed][ROUTE=scenic][TOOL=trivia_tool][MOOD=low]
        [COMMENT=User seeks calming experience]
        """
        tags = []
        comments = []

        for msg in messages:
            role = msg.get("role", "").lower()
            content = msg.get("content", "")

            # Tool type
            if role in ("trivia_tool", "route_tool", "aesthetic_tool"):
                tags.append(f"[TOOL={role}]")

            # Sentiment
            if "stressed" in content:
                tags.append("[SENTIMENT=stressed]")
            elif "calm" in content:
                tags.append("[SENTIMENT=calm]")
            elif "happy" in content:
                tags.append("[MOOD=happy]")
            elif "tired" in content:
                tags.append("[MOOD=low]")

            # Route preference
            if re.search(r"scenic|trees|nature|sunset", content, re.IGNORECASE):
                tags.append("[ROUTE=scenic]")
            elif "fastest" in content:
                tags.append("[ROUTE=fastest]")

            # Code/logic calls
            if "plot" in content or "visualize" in content:
                tags.append("[TOOL=code_mapper]")

            # Custom insight comment (for non-obvious info)
            if role == "user" and len(content.split()) > 12:
                comments.append(f"[COMMENT={content[:64]}...]")

        # Deduplicate tags
        final = list(dict.fromkeys(tags + comments))
        return "".join(final)

    def truncate_fields(self, messages: List[Dict], keys_to_keep: List[str]) -> List[Dict]:
        return [
            {k: v for k, v in m.items() if k in keys_to_keep}
            for m in messages
        ]

    def to_tags_only(self, messages: List[Dict]) -> str:
        return self.summarize_messages(messages)
