import { getAccessToken } from "./auth";

export function getWebSocketUrl(path = "/ws") {
  return `${import.meta.env.VITE_WS_BASE_URL}${path}`;
}

