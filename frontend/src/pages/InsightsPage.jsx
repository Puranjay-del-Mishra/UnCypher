import MapHeader from "../components/MapHeader";
import ChatSidebar from "../components/ChatSidebar";
import ChatWindow from "../components/ChatWindow";

export default function InsightsPage() {
  return (
    <div className="flex flex-col h-full w-full">
      {/* Sticky Map Header */}
      <div className="sticky top-0 z-50">
        <MapHeader />
      </div>

      {/* Chat layout */}
      <div className="flex flex-1 overflow-hidden">
        <ChatWindow />
      </div>
    </div>
  );
}
