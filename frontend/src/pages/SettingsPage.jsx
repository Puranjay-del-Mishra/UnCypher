export default function SettingsPage() {
  return (
    <div className="space-y-4">
      <h2 className="text-2xl font-bold">⚙️ Settings</h2>
      <p className="text-gray-600 dark:text-gray-400">
        Customize your location preferences, insight permissions, and account plan.
      </p>

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
        <div className="rounded-lg border p-4 bg-white dark:bg-zinc-800">
          <h3 className="font-medium text-gray-800 dark:text-white">🌍 Location Sharing</h3>
          <p className="text-sm text-gray-500 dark:text-gray-400">Enable or disable live location tracking.</p>
        </div>

        <div className="rounded-lg border p-4 bg-white dark:bg-zinc-800">
          <h3 className="font-medium text-gray-800 dark:text-white">🧠 Insight Feed</h3>
          <p className="text-sm text-gray-500 dark:text-gray-400">Toggle automatic AI insights based on your behavior.</p>
        </div>

        <div className="rounded-lg border p-4 bg-white dark:bg-zinc-800">
          <h3 className="font-medium text-gray-800 dark:text-white">💳 Plan Info</h3>
          <p className="text-sm text-gray-500 dark:text-gray-400">You're currently on the Pro Plan.</p>
        </div>
      </div>
    </div>
  );
}
