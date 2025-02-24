import { createContext, useState, useEffect } from 'react';
import axios from 'axios';
import config from '../config';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState(null);
  const [csrfToken, setCsrfToken] = useState('');

  useEffect(() => {
    const fetchCsrfToken = async () => {
      try {
        const response = await axios.get(`${config.API_BASE_URL}/csrf-token`, { withCredentials: true });
        setCsrfToken(response.data.token);
        axios.defaults.headers.common['X-XSRF-TOKEN'] = response.data.token;
      } catch (error) {
        console.error('Failed to fetch CSRF token:', error);
      }
    };
    fetchCsrfToken();
  }, []);

  const login = (userData) => {
    setIsAuthenticated(true);
    setUser(userData);
  };

  const logout = async () => {
    try {
      await axios.post(`${config.API_BASE_URL}/auth/logout`, {}, { withCredentials: true });
      setIsAuthenticated(false);
      setUser(null);
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, user, login, logout, csrfToken }}>
      {children}
    </AuthContext.Provider>
  );
};

export {AuthContext};
