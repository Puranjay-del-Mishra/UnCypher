from user_state import UserState
from typing import List
from tools.navigation_tool import NavigationTool

class POIFeeder:
    def __init__(self):
        self.nav_tool = NavigationTool()

    def get_nearby_poi_snippets(self, user_state: UserState, category: str = "popular places") -> List[str]:
        locality = f"{user_state.location[0]},{user_state.location[1]}"
        response = self.nav_tool.handle_poi(category, locality, mode="snippets")
        return response.get("snippets", [])
