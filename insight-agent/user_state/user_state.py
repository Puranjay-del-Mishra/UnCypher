from typing import Optional, Tuple
from dataclasses import dataclass

@dataclass
class UserState:
    user_id: str
    location: Tuple[float, float]
    page: str
    biometrics: Optional[dict] = None
    travel_history: Optional[list] = None
    recent_intent: Optional[str] = None
    timestamp: Optional[str] = None