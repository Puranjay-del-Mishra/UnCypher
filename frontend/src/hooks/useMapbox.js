import { useCallback } from 'react';

const useMapbox = (map) => {
  // Smooth camera transition
  const flyTo = useCallback((coords, options = {}) => {
    if (!map) return;
    map.flyTo({
      center: coords,
      zoom: options.zoom || map.getZoom(),
      bearing: options.bearing ?? map.getBearing(),
      pitch: options.pitch ?? map.getPitch(),
      speed: options.speed || 1.2,
      curve: 1.4,
      ...options,
    });
  }, [map]);

  // Instant jump without animation
  const jumpTo = useCallback((coords, options = {}) => {
    if (!map) return;
    map.jumpTo({
      center: coords,
      zoom: options.zoom || map.getZoom(),
      bearing: options.bearing ?? map.getBearing(),
      pitch: options.pitch ?? map.getPitch(),
    });
  }, [map]);

  // Rotate the map
  const setBearing = useCallback((bearing) => {
    if (!map) return;
    map.setBearing(bearing);
  }, [map]);

  // Reset view to default state
  const resetView = useCallback((center, zoom = 14) => {
    if (!map) return;
    map.flyTo({ center, zoom, bearing: 0, pitch: 0 });
  }, [map]);

  // Fit map view to two or more points
  const fitBounds = useCallback((pointA, pointB, padding = 40) => {
    if (!map || !pointA || !pointB) return;
    const bounds = new mapboxgl.LngLatBounds();
    bounds.extend(pointA).extend(pointB);
    map.fitBounds(bounds, { padding });
  }, [map]);

  const zoomIn = () => map?.zoomIn();
  const zoomOut = () => map?.zoomOut();

  return {
    flyTo,
    jumpTo,
    setBearing,
    resetView,
    fitBounds,
    zoomIn,
    zoomOut,
  };
};

export default useMapbox;
