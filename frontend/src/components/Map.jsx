import React, { useEffect, useState, useRef } from "react";
import { MapContainer, TileLayer, Marker, Popup, useMap, Circle } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import markerIcon from "../pngs/marker-icon.png";
import markerShadow from "../pngs/marker-shadow.png";
import markerIconRetina from "../pngs/marker-icon-2x.png";

// Fix Leaflet's default icon paths
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconUrl: markerIcon,
  iconRetinaUrl: markerIconRetina,
  shadowUrl: markerShadow,
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41],
});

// Component to update the map view based on the current position
const UpdateMapView = ({ position }) => {
  const map = useMap();
  useEffect(() => {
    if (position) {
      map.setView(position, map.getZoom());
    }
  }, [position, map]);
  return null;
};

// Component to handle routing and display route information
const RoutingControl = ({ from, to, onRouteChange }) => {
  const map = useMap();
  const routingRef = useRef(null);

  useEffect(() => {
    if (!from || !to || !map) return;

    let isMounted = true;

    import("leaflet-routing-machine").then(() => {
      if (!isMounted) return;

      if (routingRef.current) {
        map.removeControl(routingRef.current);
      }

      routingRef.current = L.Routing.control({
        waypoints: [L.latLng(from[0], from[1]), L.latLng(to[0], to[1])],
        routeWhileDragging: true,
        show: false,
        createMarker: (i, wp) => L.marker(wp.latLng, { draggable: true }),
      })
        .on("routesfound", (e) => {
          const route = e.routes[0];
          const summary = route.summary;
          onRouteChange({
            distance: (summary.totalDistance / 1000).toFixed(2) + " km",
            time: (summary.totalTime / 60).toFixed(0) + " min",
          });
        })
        .addTo(map);
    });

    return () => {
      isMounted = false;
      if (routingRef.current) {
        map.removeControl(routingRef.current);
      }
    };
  }, [from, to, map, onRouteChange]);

  return null;
};

// Component to add a search bar for geocoding
const GeocoderControl = ({ setDestination }) => {
  const map = useMap();

  useEffect(() => {
    const loadGeocoder = async () => {
      if (!window.L.Control.Geocoder) {
        // Load CSS
        const link = document.createElement("link");
        link.rel = "stylesheet";
        link.href = "https://unpkg.com/leaflet-control-geocoder/dist/Control.Geocoder.css";
        document.head.appendChild(link);

        // Load JS
        await new Promise((resolve, reject) => {
          const script = document.createElement("script");
          script.src = "https://unpkg.com/leaflet-control-geocoder/dist/Control.Geocoder.js";
          script.onload = resolve;
          script.onerror = reject;
          document.body.appendChild(script);
        });
      }

      const geocoder = L.Control.geocoder({
        defaultMarkGeocode: false,
        geocoder: L.Control.Geocoder.nominatim(),
      })
        .on("markgeocode", function (e) {
          const latlng = e.geocode.center;
          setDestination([latlng.lat, latlng.lng]);
          map.setView(latlng, 14);
        })
        .addTo(map);
    };

    loadGeocoder();

    return () => {
      const geocoderElement = document.querySelector(".leaflet-control-geocoder");
      if (geocoderElement) {
        geocoderElement.remove();
      }
    };
  }, [map, setDestination]);

  return null;
};

const Map = () => {
  const [position, setPosition] = useState(null);
  const [destination, setDestination] = useState(null);
  const [routeInfo, setRouteInfo] = useState(null);
  const mapRef = useRef();

  // Track user's live location
  useEffect(() => {
    const updatePosition = () => {
      navigator.geolocation.getCurrentPosition(
        (pos) => {
          const newPosition = [pos.coords.latitude, pos.coords.longitude];
          setPosition(newPosition);
        },
        (err) => console.error("Geolocation error:", err),
        { enableHighAccuracy: true, timeout: 5000 }
      );
    };

    updatePosition();
    const intervalId = setInterval(updatePosition, 5000);
    return () => clearInterval(intervalId);
  }, []);

  return (
    <div>
      <div style={{ height: "400px", width: "100%", borderRadius: "10px" }}>
        <MapContainer
          center={[51.505, -0.09]}
          zoom={13}
          style={{ height: "100%", width: "100%" }}
          whenCreated={(map) => (mapRef.current = map)}
          onClick={(e) => {
            const { lat, lng } = e.latlng;
            setDestination([lat, lng]);
          }}
        >
          <TileLayer
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            attribution="&copy; OpenStreetMap contributors"
          />

          {position && (
            <>
              <Marker position={position}>
                <Popup>📍 You are here!</Popup>
              </Marker>
              <Circle center={position} radius={14.48} color="blue" fillOpacity={0.1} />
              {destination && (
                <RoutingControl from={position} to={destination} onRouteChange={setRouteInfo} />
              )}
            </>
          )}

          {destination && (
            <Marker position={destination}>
              <Popup>🎯 Destination</Popup>
            </Marker>
          )}

          <UpdateMapView position={position} />
          <GeocoderControl setDestination={setDestination} />
        </MapContainer>
      </div>

      {/* Route Info Panel */}
      {routeInfo && (
        <div
          style={{
            marginTop: "10px",
            padding: "10px",
            borderRadius: "8px",
            backgroundColor: "#f0f4ff",
            color: "#333",
            fontSize: "14px",
          }}
        >
          <strong>Route Info:</strong>
          <br />
          Distance: {routeInfo.distance}
          <br />
          ETA: {routeInfo.time}
        </div>
      )}
    </div>
  );
};

export default Map;


