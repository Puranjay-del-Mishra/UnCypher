import React, { useEffect, useState, useRef } from "react";
import { MapContainer, TileLayer, Marker, Popup, useMap, Circle } from "react-leaflet";
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import markerIcon from '../pngs/marker-icon.png';
import markerShadow from '../pngs/marker-shadow.png';
import markerIconRetina from '../pngs/marker-icon-2x.png'
// Fix Leaflet's broken default icons
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconUrl: markerIcon,
  iconRetinaUrl: markerIconRetina,
  shadowUrl: markerShadow,
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});

// Component to handle map view updates
const UpdateMapView = ({ position }) => {
  const map = useMap();
  useEffect(() => {
    if (position) {
      map.setView(position, map.getZoom());
    }
  }, [position, map]);
  return null;
};

const Map = () => {
  const [position, setPosition] = useState(null);
  const mapRef = useRef();

  // Continuous location updates every 5 seconds
  useEffect(() => {
    let watchId;

    const updatePosition = () => {
      navigator.geolocation.getCurrentPosition(
        (pos) => {
          const newPos = [pos.coords.latitude, pos.coords.longitude];
          setPosition(newPos);
        },
        (err) => console.error("Error getting position:", err),
        { enableHighAccuracy: true, timeout: 5000 }
      );
    };

    // Initial position
    updatePosition();

    // Update every 5 seconds
    const intervalId = setInterval(updatePosition, 5000);

    // Cleanup
    return () => {
      clearInterval(intervalId);
      if (watchId) navigator.geolocation.clearWatch(watchId);
    };
  }, []);

  return (
    <div className="map-container" style={{ width: "100%", height: "400px", marginTop: "10px" }}>
      <MapContainer
        center={[51.505, -0.09]} // Default position
        zoom={13}
        style={{ height: "100%", width: "100%", borderRadius: "10px" }}
        whenCreated={(map) => (mapRef.current = map)}
      >
        <TileLayer
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
        />

        {position && (

          <>
          <Marker
            position={position}
            key={`${position[0]}-${position[1]}`} // Force re-render on position change
          >
            <Popup autoOpen={true}>📍 You are here!</Popup>
          </Marker>
          <Circle
                center={position}
                radius={14.48} // Use the accuracy value from your data
                color="blue"
                fillOpacity={0.1}
          />
          </>
        )}

        <UpdateMapView position={position} />
      </MapContainer>
    </div>
  );
};

export default Map;