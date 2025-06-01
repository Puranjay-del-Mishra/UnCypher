import React from "react";

/**
 * ChatMessage Component
 * @param {Object} props
 * @param {Object} props.message - Chat message object
 */
export default function ChatMessage({ message }) {
  const isUser = message.sender === "user";

  return (
    <div
      className={`max-w-xl px-4 py-2 rounded-lg ${
        isUser ? "bg-primary text-white self-end" : "bg-muted self-start"
      }`}
    >
      {message.loading ? (
        <div className="flex items-center space-x-1 animate-pulse">
          <span className="w-2 h-2 bg-foreground rounded-full" />
          <span className="w-2 h-2 bg-foreground rounded-full" />
          <span className="w-2 h-2 bg-foreground rounded-full" />
        </div>
      ) : (
        message.text
      )}
    </div>
  );
}
