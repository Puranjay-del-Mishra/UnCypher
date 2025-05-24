import { useRef } from 'react';
import { useMapboxContext } from '../../context/MapboxProvider';
import useMapbox from '../../hooks/useMapbox';

const MapControls = () => {
  const { mapInstance } = useMapboxContext();
  const {
    flyTo,
    setBearing,
    resetView,
    setZoom,
    fitBounds,
    addLayer,
    removeLayer,
  } = useMapbox(mapInstance);

  const layers = useRef({
    route: null,
    zones: {},
    overlays: {},
    users: {},
  });

  const controls = {
    // Viewport Controls
    flyTo: (center, zoom) => flyTo(center, { zoom }),
    setZoom: (zoom) => mapInstance?.setZoom(zoom),
    fitBounds: (bounds, padding = 40) => fitBounds(bounds[0], bounds[1], padding),
    setBearing: (bearing) => setBearing(bearing),
    resetView: (center = [0, 0], zoom = 3) => resetView(center, zoom),

    // Navigation
    startRoute: (geojson) => {
      if (layers.current.route) mapInstance.removeLayer('route');
      if (mapInstance.getSource('route')) mapInstance.removeSource('route');

      mapInstance.addSource('route', { type: 'geojson', data: geojson });
      mapInstance.addLayer({
        id: 'route',
        type: 'line',
        source: 'route',
        paint: {
          'line-color': '#3b82f6',
          'line-width': 5,
        },
      });

      layers.current.route = 'route';
    },

    clearRoute: () => {
      if (layers.current.route) {
        mapInstance.removeLayer('route');
        mapInstance.removeSource('route');
        layers.current.route = null;
      }
    },

    highlightRoute: (color = '#ef4444', width = 8) => {
      if (!layers.current.route) return;
      mapInstance.setPaintProperty('route', 'line-color', color);
      mapInstance.setPaintProperty('route', 'line-width', width);
    },

    // Zones
    showZone: (zoneId, geojson, color = '#f59e0b') => {
      const layerId = `zone-${zoneId}`;

      mapInstance.addSource(layerId, { type: 'geojson', data: geojson });
      mapInstance.addLayer({
        id: layerId,
        type: 'fill',
        source: layerId,
        paint: {
          'fill-color': color,
          'fill-opacity': 0.2,
        },
      });

      layers.current.zones[zoneId] = layerId;
    },

    hideZone: (zoneId) => {
      const layerId = layers.current.zones[zoneId];
      if (!layerId) return;

      mapInstance.removeLayer(layerId);
      mapInstance.removeSource(layerId);
      delete layers.current.zones[zoneId];
    },

    highlightZone: (zoneId, color = '#ef4444') => {
      const layerId = layers.current.zones[zoneId];
      if (!layerId) return;

      mapInstance.setPaintProperty(layerId, 'fill-color', color);
    },

    // Overlays
    addOverlay: (overlayId, geojson) => {
      mapInstance.addSource(overlayId, { type: 'geojson', data: geojson });
      mapInstance.addLayer({
        id: overlayId,
        type: 'heatmap',
        source: overlayId,
      });

      layers.current.overlays[overlayId] = overlayId;
    },

    removeOverlay: (overlayId) => {
      if (!layers.current.overlays[overlayId]) return;

      mapInstance.removeLayer(overlayId);
      mapInstance.removeSource(overlayId);
      delete layers.current.overlays[overlayId];
    },

    // User controls
    showUser: (userId, coords) => {
      const marker = new mapboxgl.Marker().setLngLat(coords).addTo(mapInstance);
      layers.current.users[userId] = marker;
    },

    highlightUser: (userId) => {
      const marker = layers.current.users[userId];
      if (marker) marker.getElement().classList.add('animate-bounce');
    },

    trackUser: (coords) => flyTo(coords, { zoom: 17 }),
    untrackUser: () => {},

  };

  return controls;
};

export default MapControls;
