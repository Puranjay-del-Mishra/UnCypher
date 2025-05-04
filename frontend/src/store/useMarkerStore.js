import { create } from "zustand";
import { nanoid } from "nanoid";

export const useMarkerStore = create((set, get) => ({
  markers: [],
  routes: [],

  // Markers
  addMarker: (marker) => {
    const id = marker.id || nanoid();
    const newMarker = { id, type: marker.type || "generic", ...marker };

    set((state) => ({
      markers: [...state.markers.filter((m) => m.id !== id), newMarker],
    }));

    return id;
  },

  removeMarker: (id) => {
    set((state) => ({
      markers: state.markers.filter((m) => m.id !== id),
    }));
  },

  clearMarkers: () => set({ markers: [] }),

  // Routes
  addRoute: (route) => {
    set((state) => ({
      routes: [...state.routes, route],
    }));
  },

  clearRoutes: () => set({ routes: [] }),
}));
