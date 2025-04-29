from pydantic import BaseModel
from typing import Dict, List

class InsightResponse(BaseModel):
    answer: str
    toolUsed: str
    meta: Dict

class PassiveInsightResponse(BaseModel):
    insights: List[str]
    toolUsed: str
    meta: Dict
