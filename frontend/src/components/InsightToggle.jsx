import React from 'react';

const InsightToggle = ({ allowInsight, toggleInsight, location }) => {
    return (
        <div className="insight-toggle-container">
            <button
                className={`toggle-button ${allowInsight ? 'active' : ''}`}
                onClick={toggleInsight}
            >
                <span className="toggle-icon">
                    {allowInsight ? '❌' : '🔍'}
                </span>
                <span className="toggle-text">
                    {allowInsight ? 'Disable Insights' : 'Enable Insights'}
                </span>
            </button>
            {location && (
                <p className="location-coordinates">
                    Lat: {location[0].toFixed(4)}, Lng: {location[1].toFixed(4)}
                </p>
            )}
        </div>
    );
};

export default InsightToggle;