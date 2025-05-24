 import {create} from "zustand";

export const useMarkerStore = create((set, get) => ({
  markers: [],
  routes: [],

  // 🚀 Add Marker (state-aware → skip if same)
  addMarker: (marker) => {
    set((state) => {
      const exists = state.markers.find((m) => m.id === marker.id);
      if (exists && JSON.stringify(exists) === JSON.stringify(marker)) {
        console.log("🔄 Marker unchanged → skip");
        return state;
      }

      return {
        markers: [
          ...state.markers.filter((m) => m.id !== marker.id),
          marker,
        ],
      };
    });
  },

  // 🚮 Remove Marker
  removeMarker: (id) => {
    set((state) => ({
      markers: state.markers.filter((m) => m.id !== id),
    }));
  },

  // 🛣️ Add Route (state-aware)
  addRoute: (route) => {
    set((state) => {
      const exists = state.routes.find((r) => r.id === route.id);
      if (exists && JSON.stringify(exists) === JSON.stringify(route)) {
        console.log("🔄 Route unchanged → skip");
        return state;
      }

      return {
        routes: [
          ...state.routes.filter((r) => r.id !== route.id),
          route,
        ],
      };
    });
  },

  // ❌ Clear All Routes
  clearRoutes: () => {
    set(() => ({ routes: [] }));
  },
}));
