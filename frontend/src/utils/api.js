// src/utils/api.js
import axios from "axios";
import config from "../config";

const api = axios.create({
  baseURL: config.API_BASE_URL,
  withCredentials: true,
});

let csrfToken = null;
let csrfFetched = false;
let csrfLastFetchedAt = 0;
const CSRF_TTL = 10 * 60 * 1000;

export const getCsrfToken = async () => {
  const now = Date.now();

  if (csrfToken && csrfFetched && now - csrfLastFetchedAt < CSRF_TTL) {
    return csrfToken;
  }

  try {
    const response = await axios.get(`${config.API_BASE_URL}/csrf-token`, {
      withCredentials: true,
    });

    const cookie = document.cookie
      .split("; ")
      .find((row) => row.startsWith("XSRF-TOKEN="));

    csrfToken = cookie ? decodeURIComponent(cookie.split("=")[1]) : response.data.token;
    csrfFetched = true;
    csrfLastFetchedAt = now;

    return csrfToken;
  } catch (error) {
    console.error("❌ Failed to fetch CSRF token:", error);
    throw error;
  }
};

export const resetCsrf = () => {
  csrfToken = null;
  csrfFetched = false;
  csrfLastFetchedAt = 0;
};

api.interceptors.request.use(async (config) => {
  const token = await getCsrfToken();
  config.headers["X-XSRF-TOKEN"] = token;
  return config;
});

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (
      error.response &&
      error.response.status === 403 &&
      !originalRequest._retry &&
      !originalRequest.url.includes("/auth/")
    ) {
      originalRequest._retry = true;

      resetCsrf();
      const newToken = await getCsrfToken();
      originalRequest.headers["X-XSRF-TOKEN"] = newToken;
      originalRequest.withCredentials = true;
      return api(originalRequest);
    }

    return Promise.reject(error);
  }
);

export default api;




