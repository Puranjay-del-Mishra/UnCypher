import { useEffect } from "react";
import { useMapboxContext } from "../../context/MapboxProvider";
import mapboxgl from "mapbox-gl";

const MapMarker = ({ coords, color = "blue", iconUrl = null, popupText = "" }) => {
  const { mapInstance } = useMapboxContext();

  // ✅ If map or coords are missing OR map not fully ready → do not render anything
  if (!mapInstance || !coords || !mapInstance.isStyleLoaded()) return null;

  useEffect(() => {
    if (!mapInstance || !coords || !mapInstance.isStyleLoaded()) return;

    let marker;

    if (iconUrl) {
      const el = document.createElement("div");
      el.style.backgroundImage = `url(${iconUrl})`;
      el.style.width = "40px";
      el.style.height = "40px";
      el.style.backgroundSize = "cover";
      el.style.borderRadius = "50%";
      el.style.border = "2px solid white";
      el.style.backgroundPosition = "center";

      marker = new mapboxgl.Marker({ element: el }).setLngLat([coords.lng, coords.lat]);
    } else {
      marker = new mapboxgl.Marker({ color }).setLngLat([coords.lng, coords.lat]);
    }

    if (popupText) {
      marker.setPopup(new mapboxgl.Popup().setText(popupText));
    }

    marker.addTo(mapInstance);

    return () => marker.remove();
  }, [mapInstance, coords, color, iconUrl, popupText]);

  return null;
};

export default MapMarker;
