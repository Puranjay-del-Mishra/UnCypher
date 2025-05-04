import { useEffect } from 'react';
import MapboxGeocoder from '@mapbox/mapbox-gl-geocoder';
import '@mapbox/mapbox-gl-geocoder/dist/mapbox-gl-geocoder.css';

const GeocoderControl = ({ map, onSelect }) => {
  useEffect(() => {
    if (!map) return;

    const geocoder = new MapboxGeocoder({
      accessToken: mapboxgl.accessToken,
      mapboxgl,
      placeholder: 'Search for places',
      marker: false,
    });

    map.addControl(geocoder);

    geocoder.on('result', (e) => {
      const coords = e.result.center;
      if (onSelect) onSelect(coords);
    });

    return () => {
      geocoder.off('result');
      map.removeControl(geocoder);
    };
  }, [map]);

  return null;
};

export default GeocoderControl;
