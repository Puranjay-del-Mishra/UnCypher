// utils/auth.js
import axios from "axios";

export function getAccessToken() {
  return localStorage.getItem("accessToken");
}

export function getRefreshToken() {
  return localStorage.getItem("refreshToken");
}

export function decodeJWT(token) {
  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    return payload;
  } catch (e) {
    return null;
  }
}

export async function refreshTokenIfNeeded() {
  let accessToken = localStorage.getItem("accessToken");
  const refreshToken = localStorage.getItem("refreshToken");

  if (!accessToken) return null;

  const jwtPayload = JSON.parse(atob(accessToken.split('.')[1]));
  const exp = jwtPayload.exp * 1000;
  const now = Date.now();

  if (exp - now < 2 * 60 * 1000) {
    console.log('🔄 Token close to expiry, refreshing...');

    try {
      const response = await axios.post('http://localhost:8080/auth/refresh', { refreshToken });
      const newAccessToken = response.data.accessToken;
      localStorage.setItem('accessToken', newAccessToken);
      console.log('✅ Token refreshed.');
      return newAccessToken;
    } catch (error) {
      console.error('⛔ Refresh token failed.', error);
      window.location.href = '/login';
      return null;
    }
  }

  return accessToken;
}

