from tools.guide_tool import GuideTool
from tools.convo_tool.convo_tool import ConvoTool
from tools.navigation_tool import NavigationTool
from tools.poi_refiner_tool import PoiRefinerTool

class ToolRegistry:
    """
    Central registry of all tools available to the Orchestrator.
    Provides tool descriptions for prompting and instances for execution.
    """

    def __init__(self):
        self.tools = {
            ConvoTool.name: ConvoTool(),
            NavigationTool.name: NavigationTool(),
            PoiRefinerTool.name: PoiRefinerTool()
        }

        self.tool_classes = {
            ConvoTool.name: ConvoTool,
            NavigationTool.name: NavigationTool,
            PoiRefinerTool.name: PoiRefinerTool
        }

    def get_tool(self, name):
        """
        Get tool instance by name (case insensitive)
        """
        return self.tools.get(name.lower())

    def describe_tools(self):
        """
        Describe tools for planner prompt
        """
        return "\n".join(
            f"{cls.name}: {cls.description}"
            for cls in self.tool_classes.values()
        )
