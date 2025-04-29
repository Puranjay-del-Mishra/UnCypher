import { create } from "zustand";
import { persist } from "zustand/middleware";

export const useChatStore = create(
  persist(
    (set) => ({
      userId: null,

      messages: [],

      setUserId: (newUserId) => set({ userId: newUserId }),

      setMessages: (newMessages) => set({ messages: newMessages }),

      addMessage: (message) =>
        set((state) => ({ messages: [...state.messages, message] })),

      clearMessages: () => set({ messages: [] }),
    }),
    {
      name: "chat-storage", // Key for localStorage
      partialize: (state) => ({
        userId: state.userId,
        messages: state.messages,
      }),
    }
  )
);
