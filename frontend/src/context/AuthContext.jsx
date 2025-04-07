import { createContext, useState, useEffect } from "react";
import axios from "axios";
import config from "../config";
import api from "../utils/api";         // Your Axios instance (can still use it)

const AuthContext = createContext(null);

const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [accessToken, setAccessToken] = useState(() => localStorage.getItem("accessToken"));
  const [isAuthenticated, setIsAuthenticated] = useState(null);
  // ✅ Decode JWT to extract user info (optional, based on your backend payload)
  const decodeJWT = (token) => {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload;
    } catch (e) {
      return null;
    }
  };

  // ✅ Login
const login = async (userData) => {
  try {
    const response = await api.post("/auth/login", userData);

    if (response.status !== 200) {
      throw new Error("Unauthorized"); // ensure failure is caught
    }

    const { accessToken, refreshToken } = response.data;

    localStorage.setItem("accessToken", accessToken);
    localStorage.setItem("refreshToken", refreshToken);
    setAccessToken(accessToken);

    const userPayload = decodeJWT(accessToken);
    setUser(userPayload);
    setIsAuthenticated(true);
  } catch (err) {
    setIsAuthenticated(false); // ⛔ Revoke auth flag
    setUser(null);
    throw err; // bubble up to frontend for UI error
  }
};


  // ✅ Logout
  const logout = () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    setAccessToken(null);
    setUser(null);
  };

  // ✅ Check token on load
  useEffect(() => {
    if (accessToken) {
      const payload = decodeJWT(accessToken);
      setUser(payload);
      setIsAuthenticated(true); // ✅ Ensure auth is marked as restored
    } else {
      setIsAuthenticated(false); // ✅ Handle logout/reload edge cases
    }
  }, []);


  return (
    <AuthContext.Provider value={{
      user,
      isAuthenticated: !!accessToken,
      accessToken,
      login,
      logout
    }}>
      {children}
    </AuthContext.Provider>
  );
};

export default AuthContext;
export { AuthProvider };
