import { useEffect, useRef } from 'react';
import mapboxgl from 'mapbox-gl';

const UserLocationTracker = ({ map, onLocationUpdate }) => {
  const markerRef = useRef(null);

  useEffect(() => {
    if (!map) return;

    const watchId = navigator.geolocation.watchPosition(
      (pos) => {
        const coords = [pos.coords.longitude, pos.coords.latitude];

        // Callback to parent component (optional)
        if (onLocationUpdate) onLocationUpdate(coords);

        // Animate camera to user
        map.flyTo({ center: coords, speed: 1.2, curve: 1 });

        // Marker
        if (!markerRef.current) {
          markerRef.current = new mapboxgl.Marker({ color: '#3b82f6' })
            .setLngLat(coords)
            .addTo(map);
        } else {
          markerRef.current.setLngLat(coords);
        }
      },
      (err) => console.error('Geolocation error:', err),
      { enableHighAccuracy: true, maximumAge: 10000, timeout: 5000 }
    );

    return () => navigator.geolocation.clearWatch(watchId);
  }, [map]);

  return null;
};

export default UserLocationTracker;
