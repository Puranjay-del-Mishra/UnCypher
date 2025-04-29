// Placeholder for Fitbit integration
// Later: Replace with OAuth connection + Fitbit API fetch

import { useEffect, useState } from "react";

export function useFitbitData() {
  const [fitbitData, setFitbitData] = useState({
    steps: null,
    restingHR: null,
    sleepScore: null,
  });

  useEffect(() => {
    // Simulate Fitbit data fetch — replace with real API call later
    const fetchMockFitbit = () => {
      setTimeout(() => {
        setFitbitData({
          steps: 8234,
          restingHR: 62,
          sleepScore: 85,
        });
      }, 1000); // Simulate API latency
    };

    fetchMockFitbit();
  }, []);

  return fitbitData;
}
