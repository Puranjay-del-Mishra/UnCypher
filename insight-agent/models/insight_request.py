from pydantic import BaseModel
from typing import Optional, Dict

class SensorPayload(BaseModel):
    heartRate: Optional[float]
    stressLevel: Optional[float]
    sleepScore: Optional[float]
    isInVehicle: Optional[bool]
    vehicleType: Optional[str]
    fuelLevel: Optional[float]
    isDriving: Optional[bool]
    ambientNoiseDb: Optional[float]
    temperatureCelsius: Optional[float]

class InsightRequest(BaseModel):
    userId: str
    query: str
    location: Optional[str]
    timestamp: str
    sensors: Optional[SensorPayload]
    context: Optional[Dict[str, str]]
    stream: Optional[bool] = False

class PassiveInsightRequest(BaseModel):
    userId: str
    location: str
    timestamp: str
    deviceType: Optional[str]
    meta: Optional[Dict[str, str]]
