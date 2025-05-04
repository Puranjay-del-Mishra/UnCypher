import { createContext, useContext, useState } from 'react';

const MapboxContext = createContext(null);

export const MapboxProvider = ({ children }) => {
  const [mapInstance, setMapInstance] = useState(null);

  return (
    <MapboxContext.Provider value={{ mapInstance, setMapInstance }}>
      {children}
    </MapboxContext.Provider>
  );
};

export const useMapboxContext = () => useContext(MapboxContext);
