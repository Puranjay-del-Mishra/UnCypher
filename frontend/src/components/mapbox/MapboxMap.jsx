import { useRef, useEffect } from 'react';
import mapboxgl from 'mapbox-gl';
import 'mapbox-gl/dist/mapbox-gl.css';
import { useMapboxContext } from '../../context/MapboxProvider';

mapboxgl.accessToken = import.meta.env.VITE_MAPBOX_TOKEN;

const MapboxMap = ({ children, initialCenter = [0, 0], zoom = 14 }) => {
  const mapContainerRef = useRef(null);
  const { setMapInstance, isMapReady, setIsMapReady } = useMapboxContext();

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
      if (map.isStyleLoaded()) {
        setIsMapReady(true);
      } else {
        map.once('styledata', () => setIsMapReady(true));
      }
    });

    return () => {
      map.remove();
      setMapInstance(null);
      setIsMapReady(false);
    };
  }, [setMapInstance, setIsMapReady]);

  return (
    <div className="relative w-full h-full">
      <div
        ref={mapContainerRef}
        className="w-full h-full transition-opacity duration-300"
        style={{ filter: isMapReady ? 'none' : 'blur(4px)' }}
      />

      {/* Blur Overlay */}
      {!isMapReady && (
        <div className="absolute inset-0 flex items-center justify-center bg-background/50 z-50 backdrop-blur-md">
          <div className="animate-spin h-6 w-6 border-4 border-primary border-t-transparent rounded-full" />
          <span className="ml-2 text-foreground">Loading map...</span>
        </div>
      )}

      {isMapReady && children}
    </div>
  );
};

export default MapboxMap;

