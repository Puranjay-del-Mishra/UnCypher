import { useEffect, useRef } from "react";
import { Client } from "@stomp/stompjs";
import { useAuth } from "../context/useAuth";
import { usePassiveInsightStore } from "../store/usePassiveInsightStore";
import { getWebSocketUrl } from "../utils/ws";
import { refreshTokenIfNeeded } from "../utils/auth";

export default function usePassiveInsightSocket() {
  const { user, accessToken } = useAuth();
  const userId = user?.sub;

  const {
    setPassiveInsight,
    startLoading,
    clearPassiveInsight,
  } = usePassiveInsightStore();

  const stompClientRef = useRef(null);

  useEffect(() => {
    if (!userId || !accessToken) return;

    const setupSocket = async () => {
      try {
        const freshAccessToken = await refreshTokenIfNeeded();
        if (!freshAccessToken) return;

        const wsUrl = `${getWebSocketUrl()}?token=${freshAccessToken}`;

        const stompClient = new Client({
          brokerURL: wsUrl,
          reconnectDelay: 5000,
          debug: () => {},

          onConnect: () => {
            console.log("🟢 PassiveInsight STOMP connected");

            const destination = `/user/queue/passiveInsight`;

            // Indicate we're awaiting an insight
            startLoading();

            stompClient.subscribe(destination, (message) => {
              try {
                const body = JSON.parse(message.body);
                console.log("📥 Passive Insight Received:", body);
                setPassiveInsight(userId, body);
              } catch (err) {
                console.error("❌ Failed to parse passive insight:", err);
              }
            });
          },

          onStompError: (frame) => {
            console.error("🚨 STOMP error:", frame.headers["message"]);
            console.error("Details:", frame.body);
          },
        });

        stompClientRef.current = stompClient;
        stompClient.activate();
      } catch (error) {
        console.error("🛑 PassiveInsightSocket error:", error);
      }
    };

    setupSocket();

    return () => {
      console.log("🛑 Disconnecting PassiveInsight STOMP client");
      stompClientRef.current?.deactivate();
      clearPassiveInsight(); // clean up on user/session change
    };
  }, [userId, accessToken]);
}






