import React from "react";
import Map from "./Map"; // adjust path if needed
import { useLocStore } from "../store/useLocStore"; // 👈 import the store

export default function MapHeader() {
  const { location } = useLocStore();

  return (
    <div className="rounded-xl overflow-hidden shadow border border-gray-200 dark:border-zinc-700">
      <Map position={location ? [location.lat, location.lng] : null} />
    </div>
  );
}
