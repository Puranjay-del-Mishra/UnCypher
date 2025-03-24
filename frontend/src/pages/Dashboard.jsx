import React, { useState } from "react";
import Map from "../components/Map";
import LocToggle from "../components/LocToggle";
import LocInfo from "../components/LocInfo";
import InsightToggle from "../components/InsightToggle";
import Insights from "../components/Insights";
import useUserLocation from "../hooks/useUserLocation";

const Dashboard = () => {
    const [isTracking, setIsTracking] = useState(false);
    const [allowInsight, setAllowInsight] = useState(false);
    const { location, error, loading } = useUserLocation(isTracking);

    return (
        <div className="dashboard">
            <div className="right-panel">
                <Map position={location ? [location.lat, location.lng] : null} />
            </div>
            <div className="left-panel">
                <h2>🌍 UnCypher Dashboard</h2>
                <LocToggle
                    isTracking={isTracking}
                    toggleTracking={() => setIsTracking(!isTracking)}
                />
                {error && <p className="error-message">{error}</p>}
                {loading && <p>⏳ Fetching location...</p>}
                <LocInfo locationData={location} />

                {location && (
                    <InsightToggle
                        allowInsight={allowInsight}
                        toggleInsight={() => setAllowInsight(!allowInsight)}
                        location={[location.lat, location.lng]}
                    />
                )}

                {allowInsight && location && (
                    <Insights
                        data={location}
                        allowInsight={allowInsight}
                    />
                )}
            </div>
        </div>
    );
};

export default Dashboard;