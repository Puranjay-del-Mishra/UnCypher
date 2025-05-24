import { useRef, useState, useEffect } from "react";
import { useMapboxContext } from "../../context/MapboxProvider";
import useMapControls from "../../hooks/useMapControls";
import { useMarkerStore } from "../../store/useMarkerStore";
import useMapCommandSocket from "../../hooks/useMapCommandSocket";

const isValidCoords = (coords) =>
  Array.isArray(coords) &&
  coords.length === 2 &&
  typeof coords[0] === "number" &&
  typeof coords[1] === "number" &&
  !Number.isNaN(coords[0]) &&
  !Number.isNaN(coords[1]);

const MapCommandProcessor = () => {
  const { mapInstance } = useMapboxContext();
  const controls = useMapControls();
  const { addMarker, removeMarker, addRoute, clearRoutes } = useMarkerStore();

  const lastCommandRef = useRef({});
  const flyToQueue = useRef([]);
  const [isFlying, setIsFlying] = useState(false);

  const clearFlyToQueue = () => {
    flyToQueue.current = [];
    setIsFlying(false);
  };

  const processNextFlyTo = () => {
    if (flyToQueue.current.length === 0) {
      setIsFlying(false);
      return;
    }

    const next = flyToQueue.current.shift();
    console.log("✈️ Executing queued flyTo", next);

    setIsFlying(true);
    controls.flyTo(next.coords, next.zoom || 14, { duration: 1000 });

    setTimeout(() => {
      processNextFlyTo();
    }, 1500);
  };

  const handleCommand = (command) => {
    const skipDeduplicationFor = ["fly_to", "add_marker"];
    const key = JSON.stringify(command);
    const now = Date.now();

    // ✅ Deduplicate if needed
    if (!skipDeduplicationFor.includes(command.type)) {
      if (lastCommandRef.current[key] && now - lastCommandRef.current[key] < 2000) {
        console.log("🚫 Skipping duplicate command:", command.type);
        return;
      }
      lastCommandRef.current[key] = now;
      setTimeout(() => delete lastCommandRef.current[key], 10000);
    }

    console.log("📡 Processing MapCommand", command);

    switch (command.type) {
      case "fly_to":
        if (!isValidCoords(command.coords)) {
          console.warn("❌ Invalid fly_to coords:", command.coords);
          return;
        }

        flyToQueue.current.push(command);

        if (flyToQueue.current.length > 10) {
          flyToQueue.current = flyToQueue.current.slice(-10);
        }

        if (!isFlying) {
          processNextFlyTo();
        }
        break;

      case "clear_fly_to":
        clearFlyToQueue();
        break;

      case "add_marker":
        if (!isValidCoords(command.coords)) {
          console.warn("❌ Invalid marker coords:", command.coords);
          return;
        }

        removeMarker(command.id);
        addMarker({
          id: command.id,
          coords: command.coords,
          color: command.color || "blue",
          popupText: command.popupText || "",
          iconUrl: command.iconUrl || null,
        });
        break;

      case "remove_marker":
        removeMarker(command.id);
        break;

      case "add_route":
          console.log("🧪 Raw command JSON:", JSON.stringify(command, null, 2));
        console.log("🧪 Route GeoJSON", command.geojson);
        if (!command.geojson?.geometry?.coordinates?.length) {
          console.warn("❌ Empty route, skipping:", command);
          return;
        }

        addRoute({
          id: command.id,
          geojson: command.geojson,
          color: command.color || "#3b82f6",
        });
        break;

      case "clear_routes":
        clearRoutes();
        break;

      case "set_bearing":
        controls.setBearing(command.bearing);
        break;

      case "fit_bounds":
        controls.fitBounds(command.bounds, command.padding);
        break;

      default:
        console.warn("🚧 Unknown command type:", command.type, command);
    }
  };

  // ✅ Listen to mapCommandSocket (WS)
  useMapCommandSocket((commands) => {
    clearFlyToQueue(); // Reset on every batch
    commands.forEach(handleCommand);
  });

  return null;
};

export default MapCommandProcessor;
