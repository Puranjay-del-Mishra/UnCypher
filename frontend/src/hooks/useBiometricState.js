// hooks/useBiometricState.js
import { useSmartwatchData } from "./useSmartwatchData";
import { useFitbitData } from "./useFitbitData";
// ...add more integrations here

export function useBiometricState() {
  const smartwatch = useSmartwatchData(); // returns { heartRate, sleepQuality }
  const fitbit = useFitbitData();         // returns { steps, restingHR }

  return {
    heartRate: smartwatch?.heartRate ?? fitbit?.restingHR ?? null,
    steps: fitbit?.steps ?? null,
    sleepQuality: smartwatch?.sleepQuality ?? null,
    source: smartwatch ? "smartwatch" : fitbit ? "fitbit" : "unknown",
  };
}
