import React from "react";

const LocInfo = ({ locationData }) => {
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
                </div>
            ) : (
                <p className="no-location">🔴 Location tracking is off.</p>
            )}
        </div>
    );
};

export default LocInfo;
