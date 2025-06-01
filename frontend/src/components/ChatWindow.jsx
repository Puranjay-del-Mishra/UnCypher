import { useEffect, useState, useRef } from "react";
import { useChatStore } from "../store/useChatStore";
import { useUserId } from "../hooks/useUserId";
import api from "../utils/api";
import { useMapboxContext } from "../context/MapboxProvider";
import { mapDtoToChatMessage, createChatMessage } from "../models/ChatMessage";
import ChatMessage from "./ChatMessage";

export default function ChatWindow() {
  const userId = useChatStore((s) => s.userId);
  const setUserId = useChatStore((s) => s.setUserId);
  const messages = useChatStore((s) =>
    Array.isArray(s.messages) ? s.messages : []
  );
  const setMessages = useChatStore((s) => s.setMessages);
  const addMessage = useChatStore((s) => s.addMessage);

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
        const raw = res.data;

        // ✅ Central type guard — fixes all downstream usage
        const list = Array.isArray(raw) ? raw : [];
        const formattedMessages = list
          .map(mapDtoToChatMessage)
          .filter((msg) => msg && typeof msg === "object" && msg.id && msg.sender);

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

    const userMessage = createChatMessage({
      id: Date.now().toString(),
      userId,
      sender: "user",
      text: input,
      timestamp: new Date().toISOString(),
    });

    addMessage(userMessage);
    setInput("");

    const loadingMessage = createChatMessage({
      id: `${Date.now()}-loading`,
      userId,
      sender: "assistant",
      text: "",
      timestamp: new Date().toISOString(),
      loading: true,
    });

    addMessage(loadingMessage);

    try {
      const center = mapInstance?.getCenter();
      const userLocation = center
        ? { lat: center.lat, lng: center.lng }
        : null;

      const isValidCoords = (lat, lng) =>
        typeof lat === "number" &&
        typeof lng === "number" &&
        !Number.isNaN(lat) &&
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

      const finalMessage = createChatMessage({
        id: data.messageId || loadingMessage.id, // fallback if backend doesn’t send messageId
        userId,
        sender: "assistant",
        text: assistantText,
        timestamp: new Date().toISOString(),
        loading: false,
      });

      // Replace loading message with final message
      setMessages((prev) =>
        prev.map((msg) =>
          msg.id === loadingMessage.id ? finalMessage : msg
        )
      );
    } catch (err) {
      console.error("Failed to send query", err);
      setMessages((prev) =>
        prev.map((msg) =>
          msg.id === loadingMessage.id
            ? { ...msg, text: "❌ Error retrieving response.", loading: false }
            : msg
        )
      );
    }
  }

  return (
    <div className="bg-background rounded-xl shadow-lg p-4 flex flex-col w-full max-w-2xl max-h-[400px]">
      {/* Messages Scrollable */}
      <div className="flex-1 overflow-y-auto space-y-2 mb-4">
        {Array.isArray(messages) ? (
          messages.map((msg) => (
            <ChatMessage key={msg.id} message={msg} />
          ))
        ) : (
          <div className="text-sm text-muted-foreground">No messages to display.</div>
        )}
        <div ref={bottomRef} />
      </div>

      {/* Input */}
      <form
        className="flex gap-2"
        onSubmit={(e) => {
          e.preventDefault();
          handleSend();
        }}
      >
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
