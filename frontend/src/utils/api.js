// utils/api.js
import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080"
});

// Add the interceptor
api.interceptors.request.use(async (config) => {
  let accessToken = localStorage.getItem("accessToken");
  const refreshToken = localStorage.getItem("refreshToken");

  if (accessToken) {
    const jwtPayload = JSON.parse(atob(accessToken.split('.')[1]));
    const exp = jwtPayload.exp * 1000; // JWT exp is in seconds

    const now = Date.now();

    // If the token is about to expire (within 2 minutes)
    if (exp - now < 2 * 60 * 1000) {
      console.log('🔄 Access token is expiring soon. Refreshing...');

      try {
        const response = await axios.post('http://localhost:8080/auth/refresh', {
          refreshToken: refreshToken
        });

        accessToken = response.data.accessToken;
        localStorage.setItem('accessToken', accessToken);

        console.log('✅ Access token refreshed.');
      } catch (error) {
        console.error('⛔ Token refresh failed.', error);
        // Optionally redirect to login if refresh fails
        window.location.href = '/login';
        return Promise.reject(error);
      }
    }

    config.headers["Authorization"] = `Bearer ${accessToken}`;
  }

  return config;
}, (error) => {
  return Promise.reject(error);
});

export default api;
