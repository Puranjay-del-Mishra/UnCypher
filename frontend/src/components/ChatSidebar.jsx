// components/ChatSidebar.jsx
import { useEffect, useState } from "react";
import { useChatStore } from "../store/useChatStore";

export default function ChatSidebar() {
  const { userId, chats, setChats, activeChatId, setActiveChatId } = useChatStore();

  useEffect(() => {
    async function fetchChats() {
      const res = await fetch(`/api/chats?user_id=${userId}`);
      const data = await res.json();
      setChats(data);
    }

    fetchChats();
  }, [userId]);

  return (
    <div className="w-64 p-4 rounded-xl shadow-md overflow-y-auto bg-white text-gray-800 dark:bg-zinc-900 dark:text-white">
      <h3 className="text-md font-semibold mb-3">💬 Previous Chats</h3>
      {chats.length === 0 ? (
        <p className="text-sm text-gray-500 dark:text-zinc-400">No chats yet</p>
      ) : (
        chats.map((chat) => (
          <div
            key={chat.chat_id}
            onClick={() => setActiveChatId(chat.chat_id)}
            className={`p-2 mb-2 rounded cursor-pointer text-sm transition-colors
              ${
                chat.chat_id === activeChatId
                  ? "bg-gray-200 dark:bg-zinc-800"
                  : "hover:bg-gray-100 dark:hover:bg-zinc-700"
              }`}
          >
            <div className="font-medium">{chat.title || "Untitled Chat"}</div>
            <div className="text-xs text-gray-500 dark:text-zinc-500">
              {new Date(chat.timestamp).toLocaleString()}
            </div>
          </div>
        ))
      )}
    </div>
  );
}

