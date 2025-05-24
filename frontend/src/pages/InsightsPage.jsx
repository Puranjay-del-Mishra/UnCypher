import { useEffect, useState } from "react";
import { useMapboxContext } from "../context/MapboxProvider";
import useMapControls from "../hooks/useMapControls";
import { useLocStore } from "../store/useLocStore";
import ChatWindow from "../components/ChatWindow";

export default function InsightsPage() {
  const { mapInstance } = useMapboxContext();
  const controls = useMapControls();
  const { location } = useLocStore();
  const [chatOpen, setChatOpen] = useState(true);

  useEffect(() => {
    if (location) {
      controls.flyTo([location.lng, location.lat], 14);
    }
  }, [location, mapInstance]);

  return (
    <>
      {/* Chat at bottom, center aligned */}
      <div className="absolute bottom-4 left-1/2 transform -translate-x-1/2 w-full max-w-2xl z-50">
        <div className="bg-background rounded-lg shadow-lg overflow-hidden">
          <div className="flex items-center justify-between p-2 bg-muted cursor-pointer" onClick={() => setChatOpen(!chatOpen)}>
            <span className="font-semibold">💬 Chat</span>
            <span className="text-xs">{chatOpen ? "Hide" : "Show"}</span>
          </div>

          {chatOpen && (
            <div className="max-h-[40vh] overflow-y-auto p-4">
              <ChatWindow />
            </div>
          )}
        </div>
      </div>
    </>
  );
}



