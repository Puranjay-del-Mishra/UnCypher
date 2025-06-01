import { createContext, useContext, useState, useEffect } from "react";

const MapboxContext = createContext(null);

export const MapboxProvider = ({ children }) => {
  const [mapInstance, setMapInstance] = useState(null);
  const [isMapReady, setIsMapReady] = useState(false);

  // Optional: auto-listen for map style load if mapInstance is ready
  useEffect(() => {
    if (!mapInstance) return;

    const handleReady = () => setIsMapReady(true);

    if (mapInstance.isStyleLoaded()) {
      setIsMapReady(true);
    } else {
      mapInstance.once("styledata", handleReady);
    }

    return () => {
      mapInstance.off("styledata", handleReady);
    };
  }, [mapInstance]);

  return (
    <MapboxContext.Provider
      value={{ mapInstance, setMapInstance, isMapReady, setIsMapReady }}
    >
      {children}
    </MapboxContext.Provider>
  );
};

export const useMapboxContext = () => useContext(MapboxContext);
