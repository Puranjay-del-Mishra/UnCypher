import axios from "axios";
import config from "../config";

// This instance has no interceptors or CSRF injection
const apiBare = axios.create({
  baseURL: config.API_BASE_URL,
  withCredentials: true,
});

export default apiBare;
