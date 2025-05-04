import { useMarkerStore } from "../../store/useMarkerStore";
import MapMarker from "./MapMarker";
import { useMapboxContext } from "../../context/MapboxProvider";
import { useEffect } from "react";
import mapboxgl from "mapbox-gl";

export default function GlobalLayers() {
  const { markers, routes } = useMarkerStore();
  const { mapInstance } = useMapboxContext();

  // Render routes
  useEffect(() => {
    if (!mapInstance) return;

    routes.forEach((route) => {
      if (!mapInstance.getSource(route.id)) {
        mapInstance.addSource(route.id, {
          type: "geojson",
          data: route.geojson,
        });

        mapInstance.addLayer({
          id: route.id,
          type: "line",
          source: route.id,
          paint: {
            "line-color": route.color || "#3b82f6",
            "line-width": 4,
          },
        });
      } else {
        mapInstance.getSource(route.id).setData(route.geojson);
      }
    });

    return () => {
      routes.forEach((route) => {
        if (mapInstance.getLayer(route.id)) mapInstance.removeLayer(route.id);
        if (mapInstance.getSource(route.id)) mapInstance.removeSource(route.id);
      });
    };
  }, [routes, mapInstance]);

  return (
    <>
      {markers.map((marker) => (
        <MapMarker
          key={marker.id}
          coords={marker.coords}
          color={marker.color}
          popupText={marker.popupText}
          iconUrl={marker.iconUrl}
        />
      ))}
    </>
  );
}
