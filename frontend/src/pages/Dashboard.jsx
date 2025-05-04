import { useEffect, useState } from "react";
import { useUserId } from "../hooks/useUserId";
import useUserLocation from "../hooks/useUserLocation";
import { useLocStore } from "../store/useLocStore";

import { useUserStateSync } from "../hooks/useUserStateSync";
import usePassiveInsightSocket from "../hooks/usePassiveInsightSocket";

import LocToggle from "../components/LocToggle";
import LocInfo from "../components/LocInfo";
import useMapControls from "../hooks/useMapControls";
import { useMarkerStore } from "../store/useMarkerStore";

const Dashboard = () => {
  const userId = useUserId();
  const { isTracking, setTracking, location, setLocation } = useLocStore();
  const { location: trackedLocation, error, loading } = useUserLocation(isTracking);
  const [destination, setDestination] = useState(null);

  const controls = useMapControls();
  const { addMarker, removeMarker } = useMarkerStore();

  useEffect(() => {
    if (trackedLocation) {
      setLocation(trackedLocation);
      controls.flyTo([trackedLocation.lng, trackedLocation.lat], 14);

      addMarker({
        id: "user",
        type: "user",
        coords: { lat: trackedLocation.lat, lng: trackedLocation.lng },
        color: "blue",
        popupText: "📍 You are here",
      });
    } else {
      removeMarker("user");
    }
  }, [trackedLocation]);

  useEffect(() => {
    if (destination) {
      controls.flyTo(destination, 15);
      addMarker({
        id: "destination",
        type: "destination",
        coords: { lat: destination[1], lng: destination[0] },
        color: "purple",
        popupText: "🎯 Destination",
      });
    } else {
      removeMarker("destination");
    }
  }, [destination]);

  useUserStateSync();
  usePassiveInsightSocket();

  if (!userId) return <p className="text-red-600">🚫 Could not identify user.</p>;

  return (
    <section className="space-y-8 px-4 md:px-6">
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-semibold">📍 Location Settings</h2>
        <LocToggle isTracking={isTracking} toggleTracking={() => setTracking(!isTracking)} />
      </div>

      <div className="space-y-3 text-sm">
        {error && <p className="text-red-500">⚠️ {error}</p>}
        {loading && <p>⏳ Fetching location...</p>}
        {location && <LocInfo locationData={location} />}
      </div>
    </section>
  );
};

export default Dashboard;




