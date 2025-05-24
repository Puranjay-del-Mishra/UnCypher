import { useEffect, useRef } from "react";
import { Client } from "@stomp/stompjs";
import { useAuth } from "../context/useAuth";
import { getWebSocketUrl } from "../utils/ws";
import { refreshTokenIfNeeded } from "../utils/auth";

export default function useMapCommandSocket(onCommand) {
  const { user, accessToken } = useAuth();
  const userId = user?.sub;
  const stompClientRef = useRef(null);

  useEffect(() => {
    async function setupSocket() {
      if (!userId || !accessToken) return;

      const freshAccessToken = await refreshTokenIfNeeded();
      if (!freshAccessToken) return;

      const wsUrl = `${getWebSocketUrl()}?token=${freshAccessToken}`;

      const stompClient = new Client({
        brokerURL: wsUrl,
        reconnectDelay: 5000,
        debug: () => {}, // 🔇 Prevent crash and suppress logs
        onConnect: () => {
          stompClient.subscribe(`/topic/mapCommand/${userId}`, (message) => {
            try {
              const command = JSON.parse(message.body);
              onCommand([command]); // normalize to array
            } catch (err) {
              console.error("❌ Failed to parse single MapCommand:", err);
            }
          });

          stompClient.subscribe(`/topic/mapCommandBatch/${userId}`, (message) => {
            try {
              const batch = JSON.parse(message.body);
              onCommand(batch.commands || []);
            } catch (err) {
              console.error("❌ Failed to parse MapCommandBatch:", err);
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
    }

    setupSocket();

    return () => {
      stompClientRef.current?.deactivate();
    };
  }, [userId, accessToken, onCommand]);
}


