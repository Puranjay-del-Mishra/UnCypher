import { useEffect, useRef } from 'react';
import MapboxDirections from '@mapbox/mapbox-gl-directions/dist/mapbox-gl-directions';
import '@mapbox/mapbox-gl-directions/dist/mapbox-gl-directions.css';

const RoutingControl = ({ map, origin, destination }) => {
  const directionsRef = useRef(null);

  useEffect(() => {
    if (!map || !origin || !destination) return;

    // Clean up previous
    if (directionsRef.current) {
      map.removeControl(directionsRef.current);
    }

    const directions = new MapboxDirections({
      accessToken: mapboxgl.accessToken,
      unit: 'metric',
      profile: 'mapbox/driving',
      interactive: false,
      controls: {
        inputs: false,
        instructions: true,
      },
    });

    directions.setOrigin([origin.lng, origin.lat]);
    directions.setDestination(destination);

    map.addControl(directions, 'top-left');
    directionsRef.current = directions;

    return () => {
      if (directionsRef.current) {
        map.removeControl(directionsRef.current);
        directionsRef.current = null;
      }
    };
  }, [map, origin, destination]);

  return null;
};

export default RoutingControl;
