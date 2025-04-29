import { useState, useEffect } from "react";

const useUserLocation = (isTracking) => {
    const [location, setLocation] = useState(null);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);
    const options = {
        enableHighAccuracy: true,
        timeout: 5000,
        maximumAge: 0,
    }
    useEffect(() => {
        if (!isTracking) {
            setLocation(null);
            setError(null);
            return;
        }

        setLoading(true);

        const fetchLocation = () => {
            navigator.geolocation.getCurrentPosition(
                (position) => {
                    const { latitude, longitude, accuracy } = position.coords;

                    fetch(
                        `https://nominatim.openstreetmap.org/reverse?format=json&lat=${latitude}&lon=${longitude}`
                    )
                        .then((res) => res.json())
                        .then((data) => {
                            setLocation({
                                lat: latitude,
                                lng: longitude,
                                accuracy: accuracy,
                                city: data.address?.city || "Unknown",
                                country: data.address?.country || "Unknown",
                                insights: null
                            });
                            setError(null);
                        })
                        .catch(() => setError("⚠️ Failed to fetch location details."))
                        .finally(() => setLoading(false));
                },
                () => {
                    setError("❌ Location permission denied. Please enable it.");
                    setLoading(false);
                },
                options
            );
        };

        fetchLocation();
    }, [isTracking]);

    return { location, error, loading };
};

export default useUserLocation;

