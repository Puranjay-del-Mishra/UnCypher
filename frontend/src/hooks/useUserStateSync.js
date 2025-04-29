import { useEffect, useRef } from "react";
import { useLocStore } from "../store/useLocStore";
import { useUserId } from "./useUserId";
import { useBiometricState } from "./useBiometricState";
import { getAccessToken } from "../utils/auth.js"

export function useUserStateSync() {
  const userId = useUserId();
  const { location } = useLocStore();
  const biometrics = useBiometricState();

  const lastSentState = useRef(null);

  useEffect(() => {
    if (!userId || !location) return;

    const currentState = {
      location: {
        lat: location.lat,
        lng: location.lng,
        accuracy: location.accuracy,
        city: location.city,
        country: location.country,
      },
      device: "web",
      biometrics: biometrics || {},
    };

    const hasChanged =
      !lastSentState.current ||
      JSON.stringify(lastSentState.current) !== JSON.stringify(currentState);

    if (hasChanged) {
      const token = getAccessToken();
      fetch(`${import.meta.env.VITE_API_BASE_URL}/api/state/update`, {
        method: "POST",
        headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
        body: JSON.stringify({ userId, state: currentState }),
      });
      lastSentState.current = currentState;
    }
  }, [userId, location, biometrics]);
}
