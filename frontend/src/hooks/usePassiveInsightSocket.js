import { useEffect } from "react";
import { Client } from "@stomp/stompjs";
import { useAuth } from "../context/useAuth";
import { usePassiveInsightStore } from "../store/usePassiveInsightStore";
import { getWebSocketUrl } from "../utils/ws";
import { refreshTokenIfNeeded } from "../utils/auth"; // ✅ Import your refresh helper
import { useRef } from "react";

export default function usePassiveInsightSocket() {
  const { user, accessToken } = useAuth();
  const userId = user?.sub;
  const setPassiveInsight = usePassiveInsightStore((s) => s.setPassiveInsight);
  const stompClientRef = useRef(null); // 🛠 Use ref

  useEffect(() => {
    async function setupSocket() {
      if (!userId || !accessToken) return;

      const freshAccessToken = await refreshTokenIfNeeded();
      if (!freshAccessToken) return;

      const wsUrl = `${getWebSocketUrl()}?token=${freshAccessToken}`;

      const stompClient = new Client({
        brokerURL: wsUrl,
        reconnectDelay: 5000,
        debug: (str) => console.log("🐛 STOMP:", str),

        onConnect: () => {
          console.log("🟢 STOMP connected");

          const destination = `/user/queue/passiveInsight`;
          console.log("📡 Subscribing to:", destination);

          stompClient.subscribe(destination, (message) => {
            try {
              const body = JSON.parse(message.body);
              console.log("📥 Passive insight received:", body);
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

      stompClientRef.current = stompClient; // Save client instance
      stompClient.activate();
    }

    setupSocket();

    return () => {
      console.log("🛑 Disconnecting STOMP client");
      stompClientRef.current?.deactivate();
    };
  }, [userId, accessToken]);
}




