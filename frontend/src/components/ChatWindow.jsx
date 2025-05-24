import { useEffect, useState, useRef } from "react";
import { useChatStore } from "../store/useChatStore";
import { useUserId } from "../hooks/useUserId";
import api from "../utils/api";
import { useMapboxContext } from "../context/MapboxProvider";

export default function ChatWindow() {
  const { userId, setUserId, messages, setMessages, addMessage } = useChatStore();
  const authUserId = useUserId();
  const [input, setInput] = useState("");
  const bottomRef = useRef(null);
  const { mapInstance } = useMapboxContext();

  useEffect(() => {
    if (authUserId) setUserId(authUserId);
  }, [authUserId, setUserId]);

  useEffect(() => {
    async function fetchRecentMessages() {
      if (!userId) return;

      try {
        const res = await api.get(`/api/insights/history/${userId}`);
        const data = res.data || [];

        const formattedMessages = data.map(chat => ({
          sender: chat.role,
          text: chat.content,
          timestamp: chat.timestamp,
        }));

        setMessages(formattedMessages);
      } catch (err) {
        console.error("Failed to fetch chat history", err);
        setMessages([]);
      }
    }

    fetchRecentMessages();
  }, [userId, setMessages]);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  async function handleSend() {
    if (!input.trim() || !userId) return;

    const userMessage = {
      sender: "user",
      text: input,
      timestamp: new Date().toISOString(),
    };

    addMessage(userMessage);
    setInput("");

    try {
      const center = mapInstance?.getCenter();
      const userLocation = center
        ? { lat: center.lat, lng: center.lng }
        : null;

      const isValidCoords = (lat, lng) =>
        typeof lat === "number" &&
        typeof lng === "number" &&
        !Number.isNaN(lat) &&
        !Number.isNaN(lng) &&
        (lat !== 0 || lng !== 0);

      if (!userLocation || !isValidCoords(userLocation.lat, userLocation.lng)) {
        console.warn("❌ Invalid location, skipping insight request:", userLocation);
        return;
      }

      const insightRequest = {
        userId,
        query: input,
        location: `${userLocation.lat},${userLocation.lng}`,
        timestamp: new Date().toISOString(),
        sensors: null,
        context: {
          page: "insight-tab",
          lat: userLocation.lat.toString(),
          lng: userLocation.lng.toString(),
        },
      };

      const res = await api.post("/api/insights/query", insightRequest);
      const data = res.data;

      let assistantText = "No response available.";

      if (data.answer?.trim()) {
        assistantText = data.answer;
      } else if (data.intents?.length) {
        if (data.intents.includes("navigation")) {
          assistantText = "📍 Navigation updated. Please check the map.";
        } else if (data.intents.includes("poi")) {
          assistantText = "📍 Points of interest added to the map.";
        }
      }

      const assistantMessage = {
        sender: "assistant",
        text: assistantText,
        timestamp: new Date().toISOString(),
      };

      addMessage(assistantMessage);
    } catch (err) {
      console.error("Failed to send query", err);
    }
  }

  return (
    <div className="bg-background rounded-xl shadow-lg p-4 flex flex-col w-full max-w-2xl max-h-[400px]">

      {/* Messages Scrollable */}
      <div className="flex-1 overflow-y-auto space-y-2 mb-4">
        {messages.map((msg, idx) => (
          <div
            key={idx}
            className={`max-w-xl px-4 py-2 rounded-lg ${
              msg.sender === "user" ? "bg-primary text-white self-end" : "bg-muted self-start"
            }`}
          >
            {msg.text}
          </div>
        ))}
        <div ref={bottomRef} />
      </div>

      {/* Input */}
      <form className="flex gap-2" onSubmit={(e) => {
        e.preventDefault();
        handleSend();
      }}>
        <input
          className="flex-1 p-2 bg-muted rounded text-foreground placeholder:text-muted-foreground"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder="Insights here!"
        />
        <button type="submit" className="bg-primary px-4 py-2 rounded text-white">
          Send
        </button>
      </form>
    </div>
  );
}


