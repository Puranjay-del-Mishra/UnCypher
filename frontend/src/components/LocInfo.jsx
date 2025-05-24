import React, { useEffect } from "react";
import { usePassiveInsightStore } from "../store/usePassiveInsightStore";

const LocInfo = ({ locationData }) => {
  const {
    passiveInsight,
    isLoading,
    startLoading,
    clearPassiveInsight,
  } = usePassiveInsightStore();

  useEffect(() => {
    if (!locationData) {
      clearPassiveInsight();
      return;
    }

    // Only trigger loading if passive insight isn't already set
    if (!passiveInsight) {
      startLoading();
    }
  }, [locationData]);

  const renderLocationDetails = () => {
    const { lat, lng, city, country, accuracy } = locationData;

    return (
      <div className="info-box space-y-1">
        <p><strong>Latitude:</strong> {lat?.toFixed(6) ?? "N/A"}</p>
        <p><strong>Longitude:</strong> {lng?.toFixed(6) ?? "N/A"}</p>
        <p><strong>City:</strong> {city || "N/A"}</p>
        <p><strong>Country:</strong> {country || "N/A"}</p>
        <p><strong>Accuracy:</strong> ±{accuracy?.toFixed(2) ?? "N/A"} meters</p>
      </div>
    );
  };

  const renderPassiveInsight = () => {
    if (isLoading) {
      return <p className="text-sm text-blue-500 animate-pulse">⏳ Fetching insights...</p>;
    }

    if (!passiveInsight) {
      return <p className="text-sm text-gray-500">🔎 No insights available yet.</p>;
    }

    return (
      <div className="insight-box mt-4">
        <h4>🧠 Passive Insights</h4>
        <ul className="list-disc ml-5">
          {passiveInsight.insights?.map((insight, idx) => (
            <li key={idx}>{insight}</li>
          ))}
        </ul>
        <p className="text-xs mt-2 text-gray-500">
          <strong>Tool Used:</strong> {passiveInsight.toolUsed || "N/A"}
        </p>
      </div>
    );
  };

  return (
    <div className="location-info p-4 bg-white rounded-md shadow-sm">
      <h3 className="text-lg font-semibold mb-2">📍 Your Location Info</h3>

      {locationData ? (
        <>
          {renderLocationDetails()}
          {renderPassiveInsight()}
        </>
      ) : (
        <p className="text-red-500 text-sm">🔴 Location tracking is off.</p>
      )}
    </div>
  );
};

export default LocInfo;
