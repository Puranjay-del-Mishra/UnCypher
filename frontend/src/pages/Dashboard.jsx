import React, { useState } from "react";
import Map from "../components/Map";
import LocToggle from "../components/LocToggle";
import LocInfo from "../components/LocInfo";
import useUserLocation from "../hooks/useUserLocation";

const Dashboard = () => {
  const [isTracking, setIsTracking] = useState(false);
  const { location, error, loading } = useUserLocation(isTracking);

  return (
    <section className="space-y-8 px-4 md:px-6">
      {/* Section Title + Toggle */}
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-semibold text-gray-800 dark:text-white">
          📍 Location Settings
        </h2>
        <LocToggle
          isTracking={isTracking}
          toggleTracking={() => setIsTracking((prev) => !prev)}
        />
      </div>

      {/* Location Info */}
      <div className="space-y-3 text-sm text-gray-800 dark:text-gray-100">
        {error && (
          <p className="text-red-500 font-medium">⚠️ {error}</p>
        )}
        {loading && (
          <p className="text-gray-500">⏳ Fetching location...</p>
        )}
        {location && (
          <LocInfo locationData={location} />
        )}
      </div>

      {/* Map Block */}
      <div className="rounded-xl overflow-hidden shadow border border-gray-200 dark:border-zinc-700">
        <Map position={location ? [location.lat, location.lng] : null} />
      </div>
    </section>
  );
};

export default Dashboard;

