import React from "react";

const LocToggle = ({ isTracking, toggleTracking }) => {
    return (
        <div className="toggle-container">
            <label className="switch">
                <input type="checkbox" checked={isTracking} onChange={toggleTracking} />
                <span className="slider"></span>
            </label>
            <p>{isTracking ? "📍 Location Enabled" : "❌ Location Disabled"}</p>
        </div>
    );
};

export default LocToggle;
