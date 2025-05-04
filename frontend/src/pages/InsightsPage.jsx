import { useEffect } from "react";
import { useMapboxContext } from "../context/MapboxProvider";
import useMapControls from "../hooks/useMapControls";
import { useLocStore } from "../store/useLocStore";
import ChatWindow from "../components/ChatWindow";

export default function InsightsPage() {
  const { mapInstance } = useMapboxContext();
  const controls = useMapControls();
  const { location } = useLocStore();

  useEffect(() => {
    if (location) {
      controls.flyTo([location.lng, location.lat], 14);
    }
  }, [location, mapInstance]);

  return (
    <div className="flex flex-col h-full w-full">
      <div className="flex flex-1 overflow-hidden relative">
        <ChatWindow />
      </div>
    </div>
  );
}


