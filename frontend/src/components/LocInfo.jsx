import React from "react";
import { usePassiveInsightStore } from "../store/usePassiveInsightStore";

const LocInfo = ({ locationData }) => {
    const passiveInsight = usePassiveInsightStore(state => state.passiveInsight);

  return (
    <div className="location-info">
      <h3>📍 Your Location Info</h3>
      {locationData ? (
        <div className="info-box">
          <p><strong>Latitude:</strong> {locationData.lat.toFixed(6)}</p>
          <p><strong>Longitude:</strong> {locationData.lng.toFixed(6)}</p>
          <p><strong>City:</strong> {locationData.city || "N/A"}</p>
          <p><strong>Country:</strong> {locationData.country || "N/A"}</p>
          <p><strong>Accuracy:</strong> ±{locationData.accuracy.toFixed(2)} meters</p>

          {passiveInsight ? (
            <div className="insight-box mt-4">
              <h4>🧠 Passive Insights</h4>
              <ul className="list-disc ml-5">
                {passiveInsight.insights?.map((insight, idx) => (
                  <li key={idx}>{insight}</li>
                ))}
              </ul>
              <p className="text-xs mt-2 text-gray-500">
                <strong>Tool Used:</strong> {passiveInsight.toolUsed}
              </p>
            </div>
          ) : (
            <p className="text-sm text-gray-500">🔎 No insights available yet.</p>
          )}
        </div>
      ) : (
        <p className="no-location text-red-500">🔴 Location tracking is off.</p>
      )}
    </div>
  );
};

export default LocInfo;

