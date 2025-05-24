import { useEffect, useRef } from "react";
import mapboxgl from "mapbox-gl";
import { useMarkerStore } from "../../store/useMarkerStore";
import { useMapboxContext } from "../../context/MapboxProvider";

export default function GlobalLayers() {
  const { markers, routes } = useMarkerStore();
  const { mapInstance } = useMapboxContext();
  const markerRefs = useRef({}); // store live marker instances

  // 🚀 Render markers
  useEffect(() => {
    if (!mapInstance) return;

    markers.forEach((marker) => {
      if (!marker.coords) return;

      // Cleanup previous instance
      if (markerRefs.current[marker.id]) {
        markerRefs.current[marker.id].remove();
        delete markerRefs.current[marker.id];
      }

      const el = document.createElement("div");
      el.style.width = "14px";
      el.style.height = "14px";
      el.style.borderRadius = "50%";
      el.style.backgroundColor = marker.color || "blue";

      const popup = marker.popupText
        ? new mapboxgl.Popup({ offset: 25 }).setText(marker.popupText)
        : null;

      const instance = new mapboxgl.Marker(el)
        .setLngLat(marker.coords)
        .setPopup(popup || undefined)
        .addTo(mapInstance);

      markerRefs.current[marker.id] = instance;
    });

    return () => {
      Object.values(markerRefs.current).forEach((m) => m.remove());
      markerRefs.current = {};
    };
  }, [markers, mapInstance]);

  // 🛣️ Render route layers
  useEffect(() => {
    if (!mapInstance || !mapInstance.isStyleLoaded()) return;

    routes.forEach((route) => {
      const { id, geojson, color } = route;

      // ✅ Support both Feature and FeatureCollection
      const isValid =
        geojson &&
        (geojson.type === "Feature" && geojson.geometry?.type === "LineString") ||
        geojson.type === "FeatureCollection";

      if (!isValid) {
        console.warn("❌ Invalid route GeoJSON:", route);
        return;
      }

      if (mapInstance.getLayer(id)) mapInstance.removeLayer(id);
      if (mapInstance.getSource(id)) mapInstance.removeSource(id);

      mapInstance.addSource(id, {
        type: "geojson",
        data: geojson,
      });

      mapInstance.addLayer({
        id,
        type: "line",
        source: id,
        layout: {
          "line-join": "round",
          "line-cap": "round",
        },
        paint: {
          "line-color": color || "#3b82f6",
          "line-width": 4,
        },
      });
    });

    return () => {
      routes.forEach((route) => {
        const { id } = route;
        if (mapInstance.getLayer(id)) mapInstance.removeLayer(id);
        if (mapInstance.getSource(id)) mapInstance.removeSource(id);
      });
    };
  }, [routes, mapInstance]);

  return null; // all imperative
}