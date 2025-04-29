from pydantic import BaseModel

class PlanStep(BaseModel):
    TOOL_NAME: str
    INPUT_ID: str
    REASON: str

    @property
    def tool_name(self):
        return self.TOOL_NAME.lower()

    @property
    def input_id(self):
        return self.INPUT_ID

    @property
    def reason(self):
        return self.REASON
