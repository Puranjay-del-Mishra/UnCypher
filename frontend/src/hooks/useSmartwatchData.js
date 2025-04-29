import { useEffect, useState } from "react";

/**
 * Placeholder for smartwatch integration.
 * Eventually, this could connect via:
 * - Web Bluetooth
 * - A native bridge (Electron / PWA / mobile companion)
 * - Cloud sync via vendor APIs
 */
export function useSmartwatchData() {
  const [smartwatchData, setSmartwatchData] = useState({
    heartRate: null,
    sleepQuality: null,
    lastUpdated: null,
  });

  useEffect(() => {
    // Mock data simulation — replace with real data stream later
    const interval = setInterval(() => {
      const fakeHeartRate = Math.floor(60 + Math.random() * 20); // 60–80 bpm
      const fakeSleepQuality = Math.floor(70 + Math.random() * 20); // 70–90 score

      setSmartwatchData({
        heartRate: fakeHeartRate,
        sleepQuality: fakeSleepQuality,
        lastUpdated: new Date().toISOString(),
      });
    }, 5000); // Simulate new data every 5s

    return () => clearInterval(interval);
  }, []);

  return smartwatchData;
}
