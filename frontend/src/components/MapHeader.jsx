import { useLocStore } from "../store/useLocStore";

export default function MapHeader() {
  const { location } = useLocStore();

  return (
    <div className="w-full bg-white dark:bg-zinc-900 p-4 shadow border-b border-gray-200 dark:border-zinc-700">
      <h2 className="text-lg font-semibold">📍 User Location</h2>
      {location ? (
        <p>
          Latitude: <strong>{location.lat}</strong>, Longitude: <strong>{location.lng}</strong>
        </p>
      ) : (
        <p className="text-gray-500">User location not available.</p>
      )}
    </div>
  );
}
