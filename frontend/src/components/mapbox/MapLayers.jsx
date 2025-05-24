import { useEffect } from 'react';

const MapLayers = ({ map, userCoords, geojsonZones = null }) => {
  useEffect(() => {
    if (!map || !userCoords) return;

    const userId = 'user-accuracy-circle';
    const zoneId = 'geofence-zones';

    const circleSource = {
      type: 'geojson',
      data: {
        type: 'FeatureCollection',
        features: [
          {
            type: 'Feature',
            properties: {},
            geometry: {
              type: 'Point',
              coordinates: [userCoords.lng, userCoords.lat],
            },
          },
        ],
      },
    };

    const circleLayer = {
      id: userId,
      type: 'circle',
      source: userId,
      paint: {
        'circle-radius': 20,
        'circle-color': '#3b82f6',
        'circle-opacity': 0.1,
        'circle-stroke-width': 1,
        'circle-stroke-color': '#3b82f6',
      },
    };

    // Add user accuracy circle
    if (!map.getSource(userId)) {
      map.addSource(userId, circleSource);
      map.addLayer(circleLayer);
    } else {
      const source = map.getSource(userId);
      if (source.setData) source.setData(circleSource.data);
    }

    // Add optional GeoJSON overlays
    if (geojsonZones && !map.getSource(zoneId)) {
      map.addSource(zoneId, { type: 'geojson', data: geojsonZones });
      map.addLayer({
        id: zoneId,
        type: 'fill',
        source: zoneId,
        paint: {
          'fill-color': '#f43f5e',
          'fill-opacity': 0.15,
        },
      });
    }

    return () => {
      if (map.getLayer(userId)) map.removeLayer(userId);
      if (map.getSource(userId)) map.removeSource(userId);
      if (map.getLayer(zoneId)) map.removeLayer(zoneId);
      if (map.getSource(zoneId)) map.removeSource(zoneId);
    };
  }, [map, userCoords, geojsonZones]);

  return null;
};

export default MapLayers;
