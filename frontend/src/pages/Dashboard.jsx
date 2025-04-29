import { useEffect } from "react";
import { useUserId } from "../hooks/useUserId";
import useUserLocation from "../hooks/useUserLocation";
import { useLocStore } from "../store/useLocStore";

import { useUserStateSync } from "../hooks/useUserStateSync";
import usePassiveInsightSocket from "../hooks/usePassiveInsightSocket";

import LocToggle from "../components/LocToggle";
import LocInfo from "../components/LocInfo";
import Map from "../components/Map";

const Dashboard = () => {
  const userId = useUserId();
  const {
    isTracking,
    setTracking,
    location,
    setLocation,
  } = useLocStore();

  const {
    location: trackedLocation,
    error,
    loading,
  } = useUserLocation(isTracking);

  // Sync location into global state
  useEffect(() => {
    if (trackedLocation) setLocation(trackedLocation);
  }, [trackedLocation]);

  // Global passive features
  useUserStateSync(); // → Tracks location + biometrics & POSTs to backend
  usePassiveInsightSocket(); // → Receives passive insights over WebSocket

  if (!userId) {
    return <p className="text-red-600">🚫 Could not identify user.</p>;
  }

  return (
    <section className="space-y-8 px-4 md:px-6">
      {/* Section Title + Toggle */}
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-semibold text-gray-800 dark:text-white">
          📍 Location Settings
        </h2>
        <LocToggle
          isTracking={isTracking}
          toggleTracking={() => setTracking(!isTracking)}
        />
      </div>

      {/* Location Info Block */}
      <div className="space-y-3 text-sm text-gray-800 dark:text-gray-100">
        {error && <p className="text-red-500 font-medium">⚠️ {error}</p>}
        {loading && <p className="text-gray-500">⏳ Fetching location...</p>}
        {location && <LocInfo locationData={location} />}
      </div>

      {/* Map Section */}
      <div className="rounded-xl overflow-hidden shadow border border-gray-200 dark:border-zinc-700">
        {location ? (
          <Map position={[location.lat, location.lng]} />
        ) : (
          <div className="text-zinc-500 text-sm p-4">
            📍 Location not available. Enable tracking above to view the map.
          </div>
        )}
      </div>
    </section>
  );
};

export default Dashboard;
