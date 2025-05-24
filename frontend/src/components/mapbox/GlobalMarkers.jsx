import { useMarkerStore } from "../../store/useMarkerStore";
import MapMarker from "./MapMarker";

export default function GlobalMarkers() {
  const { markers } = useMarkerStore();

  return (
    <>
      {markers.map((marker) => (
        <MapMarker
          key={marker.id}
          coords={marker.coords}
          color={marker.color}
          popupText={marker.popupText}
          iconUrl={marker.iconUrl}
        />
      ))}
    </>
  );
}
