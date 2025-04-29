from tools.guide_tool import GuideTool
from tools.convo_tool.convo_tool import ConvoTool

class ToolRegistry:
    """
    Central registry of all tools available to the Orchestrator.
    Provides tool descriptions for prompting and instances for execution.
    """

    def __init__(self):
        self.tools = {
            GuideTool.name: GuideTool(),
            ConvoTool.name: ConvoTool(),  # 🆕 Register the convo_tool
        }

        self.tool_classes = {
            GuideTool.name: GuideTool,
            ConvoTool.name: ConvoTool,  # 🆕 Add description
        }

    def get_tool(self, name: str):
        return self.tools.get(name)

    def describe_tools(self) -> str:
        return "\n".join(
            f"{cls.name}: {cls.description}"
            for cls in self.tool_classes.values()
        )
