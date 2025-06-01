import { create } from "zustand";
import { persist } from "zustand/middleware";

export const useChatStore = create(
  persist(
    (set, get) => ({
      userId: null,

      messages: [],

      setUserId: (newUserId) => set({ userId: newUserId }),

      setMessages: (newMessages) => {
        if (!Array.isArray(newMessages)) {
          console.warn("[useChatStore] Tried to set non-array messages:", newMessages);
          return;
        }
        set({ messages: newMessages });
      },

      addMessage: (message) => {
        const current = get().messages;
        if (!Array.isArray(current)) {
          console.warn("[useChatStore] Invalid current messages state, resetting.");
          set({ messages: [message] });
        } else {
          set({ messages: [...current, message] });
        }
      },

      clearMessages: () => set({ messages: [] }),
    }),
    {
      name: "chat-storage",
      partialize: (state) => ({
        userId: state.userId,
        messages: Array.isArray(state.messages) ? state.messages : [],
      }),
    }
  )
);

