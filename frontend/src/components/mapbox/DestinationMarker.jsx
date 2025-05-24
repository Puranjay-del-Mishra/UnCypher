import { useEffect, useRef } from 'react';
import mapboxgl from 'mapbox-gl';

const DestinationMarker = ({ map, coords }) => {
  const markerRef = useRef(null);

  useEffect(() => {
    if (!map || !coords) return;

    if (!markerRef.current) {
      markerRef.current = new mapboxgl.Marker({ color: '#ef4444' })
        .setLngLat(coords)
        .setPopup(new mapboxgl.Popup().setText('🎯 Destination'))
        .addTo(map);
    } else {
      markerRef.current.setLngLat(coords);
    }

    return () => {
      if (markerRef.current) {
        markerRef.current.remove();
        markerRef.current = null;
      }
    };
  }, [map, coords]);

  return null;
};

export default DestinationMarker;
