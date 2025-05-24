import { create } from "zustand";

export const usePassiveInsightStore = create((set) => ({
  passiveInsight: null,
  isLoading: false,

  startLoading: () => set({ isLoading: true }),

  setPassiveInsight: (userId, data) => {
    const cleanData = data?.payload || data;
    set({
      passiveInsight: {
        userId,
        ...cleanData,
      },
      isLoading: false,
    });
  },

  clearPassiveInsight: () => set({ passiveInsight: null, isLoading: false }),
}));
