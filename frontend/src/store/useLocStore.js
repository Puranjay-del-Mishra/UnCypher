import { create } from "zustand";

export const useLocStore = create((set) => ({
  location: null,
  isTracking: false,

  setLocation: (location) => set({ location }),
  setTracking: (value) => set({ isTracking: value }),
}));
