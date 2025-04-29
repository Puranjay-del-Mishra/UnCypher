import { create } from "zustand";

export const usePassiveInsightStore = create((set) => ({
  passiveInsight: null,

  setPassiveInsight: (userId, data) =>
    set(() => {
      const cleanData = data.payload ? data.payload : data;
      return {
        passiveInsight: {
          userId,
          ...cleanData,
        },
      };
    }),
}));

