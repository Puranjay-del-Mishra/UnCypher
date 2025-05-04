import { useRef, useEffect, useState } from 'react';
import mapboxgl from 'mapbox-gl';
import 'mapbox-gl/dist/mapbox-gl.css';
import { useMapboxContext } from '../../context/MapboxProvider';

mapboxgl.accessToken = import.meta.env.VITE_MAPBOX_TOKEN;

const MapboxMap = ({ children, initialCenter = [0, 0], zoom = 14 }) => {
  const mapContainerRef = useRef(null);
  const { setMapInstance } = useMapboxContext();
  const [mapLoaded, setMapLoaded] = useState(false);

  useEffect(() => {
    const map = new mapboxgl.Map({
      container: mapContainerRef.current,
      style: 'mapbox://styles/mapbox/streets-v11',
      center: initialCenter,
      zoom,
      pitch: 45,
      antialias: true,
    });

    map.addControl(new mapboxgl.NavigationControl());
    map.addControl(new mapboxgl.FullscreenControl());
    map.addControl(new mapboxgl.ScaleControl());

    map.once('load', () => {
      setMapInstance(map);
      setMapLoaded(true);
    });

    return () => {
      map.remove();
      setMapInstance(null);
    };
  }, [setMapInstance]);

  return (
    <div className="relative w-full h-full">
      <div ref={mapContainerRef} className="w-full h-full" />
      {mapLoaded && children}
    </div>
  );
};

export default MapboxMap;
