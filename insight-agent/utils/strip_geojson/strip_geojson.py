def strip_geojson_geometry(result: dict) -> dict:
    """
    Removes geojson.geometry.coordinates and inserts a minimal description
    for planner-friendly prompt context.
    """
    if not isinstance(result, dict):
        return result

    result = dict(result)  # shallow copy
    if "map_commands" in result:
        for cmd in result["map_commands"]:
            if isinstance(cmd, dict) and "geojson" in cmd:
                geo = cmd["geojson"]
                if "geometry" in geo:
                    geo["geometry"] = {
                        "coordinates": "Geometry omitted for brevity. Refer to map commands, user query, and metadata for spatial context."
                    }
    return result
