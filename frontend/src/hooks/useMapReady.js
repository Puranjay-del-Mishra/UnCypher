import { useEffect, useState } from "react";

/**
 * Hook that returns true when the Mapbox map's style is fully loaded and stable.
 * Automatically retries and listens to 'styledata' events if not yet ready.
 *
 * @param {mapboxgl.Map} mapInstance - the Mapbox instance from context
 * @returns {boolean} isMapReady - true when style is fully loaded
 */
export default function useMapReady(mapInstance) {
  const [isReady, setIsReady] = useState(false);

  useEffect(() => {
    if (!mapInstance) return;

    let retryInterval;

    const checkReady = () => {
      if (mapInstance.isStyleLoaded()) {
        clearInterval(retryInterval);
        setIsReady(true);
      }
    };

    // Initial check
    if (mapInstance.isStyleLoaded()) {
      setIsReady(true);
    } else {
      // Listen to one-time style load event
      mapInstance.once("styledata", checkReady);

      // Fallback polling in case event is missed
      retryInterval = setInterval(checkReady, 300); // every 300ms
    }

    return () => {
      clearInterval(retryInterval);
    };
  }, [mapInstance]);

  return isReady;
}
