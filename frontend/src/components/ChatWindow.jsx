import { useEffect, useState, useRef } from "react";
import { useChatStore } from "../store/useChatStore";
import { useUserId } from "../hooks/useUserId";
import api from "../utils/api";

export default function ChatWindow() {
  const { userId, setUserId, messages, setMessages, addMessage } = useChatStore();
  const authUserId = useUserId();
  const [input, setInput] = useState("");
  const [loadingMore, setLoadingMore] = useState(false);
  const [visibleCount, setVisibleCount] = useState(6);
  const bottomRef = useRef(null);

  // Set userId when available
  useEffect(() => {
    if (authUserId) {
      setUserId(authUserId);
    }
  }, [authUserId, setUserId]);

  // Fetch messages from server
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

  // Auto-scroll to bottom when new messages come
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
      const insightRequest = {
        userId,
        query: input,
        location: null,
        timestamp: new Date().toISOString(),
        sensors: null,
        context: { page: "insight-tab" },
      };

      const res = await api.post("/api/insights/query", insightRequest);
      const data = res.data;

      const assistantMessage = {
        sender: "assistant",
        text: data.answer || "No response available.",
        timestamp: new Date().toISOString(),
      };

      addMessage(assistantMessage);
    } catch (err) {
      console.error("Failed to send query", err);
    }
  }

  function handleLoadMore() {
    if (loadingMore) return;
    setLoadingMore(true);

    const prevHeight = bottomRef.current?.parentElement?.scrollHeight || 0;

    setTimeout(() => {
      setVisibleCount((prev) => prev + 6);
      setLoadingMore(false);

      const newHeight = bottomRef.current?.parentElement?.scrollHeight || 0;
      const scrollDiff = newHeight - prevHeight;
      bottomRef.current?.parentElement?.scrollBy({ top: scrollDiff, behavior: "smooth" });
    }, 500);
  }


  const visibleMessages = messages.slice(-visibleCount);

  return (
    <div className="flex-1 bg-background p-4 rounded-xl shadow-md flex flex-col text-foreground">
      <div className="flex-1 overflow-y-auto space-y-2 mb-4">
        {messages.length > visibleMessages.length && (
          <button
            onClick={handleLoadMore}
            disabled={loadingMore}
            className="flex items-center gap-2 text-xs text-muted-foreground underline mb-2"
          >
            {loadingMore ? (
              <>
                <svg className="animate-spin h-4 w-4" viewBox="0 0 24 24">
                  <circle
                    className="opacity-25"
                    cx="12"
                    cy="12"
                    r="10"
                    stroke="currentColor"
                    strokeWidth="4"
                  ></circle>
                  <path
                    className="opacity-75"
                    fill="currentColor"
                    d="M4 12a8 8 0 018-8v8H4z"
                  ></path>
                </svg>
                Loading...
              </>
            ) : (
              "Load more"
            )}
          </button>
        )}

        {visibleMessages.map((msg, idx) => (
          <div
            key={idx}
            className={`max-w-xl px-4 py-2 rounded-lg ${
              msg.sender === "user"
                ? "bg-primary text-white self-end"
                : "bg-muted self-start"
            }`}
          >
            {msg.text}
          </div>
        ))}
        <div ref={bottomRef} />
      </div>

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






