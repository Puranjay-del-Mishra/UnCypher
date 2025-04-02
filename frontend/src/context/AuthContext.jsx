import { createContext, useState, useEffect } from "react";
import axios from "axios";
import config from "../config";
import api, {resetCsrf, getCsrfToken} from "../utils/api.js"
const AuthContext = createContext(null);
import getCookie from '../utils/cookie'; // Assumes you already have this
import apiBare from '../utils/apiBare'

const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState(null);

  // ✅ Login function
  const login = async (userData) => {
    try {
//       console.log("Login called with user data:", userData);
//       console.log("Current CSRF Token:", csrfToken);
      resetCsrf();              // Clear stale token
      await getCsrfToken();
      const response = await api.post(
        `${config.API_BASE_URL}/auth/login`,
        userData,
        {
            withCredentials: true
        }
      );
      resetCsrf();              // Clear stale token
      await getCsrfToken();
      console.log("✅ Login successful:");
      setIsAuthenticated(true);
      setUser(response.data.user);
    } catch (err) {
      console.error("❌ Login Failed!", err);
      console.error("🔍 Full Axios Error:", err.toJSON ? err.toJSON() : err);
    }
  };

  // ✅ Logout function
  const logout = async () => {
    try {
      await api.post(
        `${config.API_BASE_URL}/auth/logout`,
        {},
      );
      setIsAuthenticated(false);
      setUser(null);

      console.log("🔄 Resetting CSRF token after logout...");
      resetCsrf();
    } catch (error) {
      console.error("❌ Logout failed:", error);
    }
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, user, login, logout}}>
      {children}
    </AuthContext.Provider>
  );
};

export default AuthContext;
export {AuthProvider};

