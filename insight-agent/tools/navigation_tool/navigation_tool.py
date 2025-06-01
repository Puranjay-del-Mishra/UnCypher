import requests
from typing import Union, Dict, List, Any

class NavigationTool:
    name = "navigation_tool"
    description = (
        "Handles navigation and mapping related user queries using POI refiner tools output"
        "Use for setting destinations (e.g. 'navigate to the ABC'), showing nearby POIs "
        "(e.g. 'show restaurants near me'). or setting markers(eg Set a marker at my home!)"
    )

    def run(self, input_data: Union[str, Dict[str, Any]]) -> dict:
        if isinstance(input_data, str):
            lines = input_data.split("\n")
            mode = lines[0].strip().lower() if lines else "poi"
            target = lines[1].strip() if len(lines) > 1 else ""
            locality = lines[2].strip() if len(lines) > 2 else "Unknown"

            if mode == "poi":
                return self.handle_poi(target, locality)
            elif mode == "route":
                return self.handle_route(target, locality)
            else:
                return {"error": f"Unknown mode '{mode}'"}
        elif isinstance(input_data, dict):
            mode = input_data.get("mode")
            if mode == "batch":
                instructions = input_data.get("instructions", [])
                locality = input_data.get("locality", "")
                return self.handle_instruction_list(instructions, locality)
            else:
                return {"error": f"Unsupported mode '{mode}' in dict input."}
        return {"error": "Invalid input type"}

    def handle_instruction_list(self, instructions: List[Dict[str, Any]], locality: str) -> dict:
        map_commands = []

        try:
            origin_coords = [float(x.strip()) for x in locality.split(",")]
        except Exception as e:
            print(f"[NavigationTool] ⚠️ Failed to parse locality '{locality}': {e}")
            origin_coords = [-79.38, 43.65]  # fallback coords

        for inst in instructions:
            action = inst.get("action")
            destination = inst.get("destination")
            popup = inst.get("popup", destination)
            color = inst.get("color", "blue")

            try:
                # Step 1: Resolve destination
                poi_response = requests.post(
                    "http://localhost:8080/api/destination/resolve",
                    json={
                        "userId": "ai-agent",
                        "locality": locality,
                        "category": destination
                    },
                    timeout=5
                )
                poi_response.raise_for_status()
                poi_data = poi_response.json()
                resolved_pois = poi_data.get("commands", [])
                match = next((p for p in resolved_pois if "coords" in p), None)
                if not match:
                    continue

                dest_coords = match["coords"]

                # Step 2: Handle command
                if action == "marker":
                    map_commands.append({
                        "type": "add_marker",
                        "coords": dest_coords,
                        "popupText": popup,
                        "color": color,
                        "id": "marker-" + destination.lower().replace(" ", "-")
                    })

                elif action == "fly_to":
                    map_commands.append({
                        "type": "fly_to",
                        "coords": dest_coords,
                        "zoom": 14
                    })

                elif action == "route":
                    # 🔁 Request real route from backend
                    route_res = requests.post(
                        "http://localhost:8080/api/navigation/route",
                        json={
                            "origin": origin_coords[::-1],
                            "destination": dest_coords,
                            "mode": "car"
                        },
                        timeout=8
                    )
                    route_res.raise_for_status()
                    route_cmd = route_res.json()
                    map_commands.append(route_cmd)

            except Exception as e:
                print(f"[NavigationTool] ⚠️ Failed to process instruction: {e}")

        return {
            "map_commands": map_commands,
            "mode": "batch",
            "instruction_count": len(map_commands)
        }

    def handle_poi(self, category: str, locality: str, mode: str = "markers") -> dict:
        """Handles POI queries. Mode can be 'markers' (default) or 'snippets'."""
        print(f"[NavigationTool] 📍 Requesting POIs for category '{category}' in locality '{locality}', mode: {mode}")
        try:
            endpoint = (
                "http://localhost:8080/api/poi/resolve-category"
                if mode == "markers" else
                "http://localhost:8080/api/poi/resolve-snippets"
            )

            res = requests.post(
                endpoint,
                json={
                    "userId": "ai-agent",
                    "locality": locality,
                    "category": category
                },
                timeout=5
            )
            res.raise_for_status()
            data = res.json()

            if mode == "markers":
                return {
                    "map_commands": data.get("commands", []),
                    "mode": "poi",
                    "category": category,
                    "locality": locality
                }
            else:
                return {
                    "snippets": data.get("snippets", []),
                    "mode": "poi-snippets",
                    "category": category,
                    "locality": locality
                }

        except Exception as e:
            print(f"[NavigationTool] ❌ Error fetching POIs: {e}")
            return {
                "error": f"Failed to fetch POIs: {str(e)}",
                "map_commands": [] if mode == "markers" else [],
                "snippets": [] if mode == "snippets" else []
            }

    def handle_route(self, destination: str, locality: str) -> dict:
        """Default route handler (used by string-based flow)"""
        return self.handle_instruction_list([
            {
                "action": "route",
                "destination": destination,
                "popup": f"Route to {destination}",
                "color": "yellow"
            },
            {
                "action": "marker",
                "destination": destination,
                "popup": f"{destination}",
                "color": "blue"
            }
        ], locality)
