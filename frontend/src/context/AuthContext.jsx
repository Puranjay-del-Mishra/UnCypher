import { createContext, useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import config from '../config';

const AuthContext = createContext(null);

// ✅ Fetch CSRF Token Function
const getCsrfTokenFromCookie = () => {
  const cookie = document.cookie
    .split("; ")
    .find((row) => row.startsWith("XSRF-TOKEN="));
  return cookie ? decodeURIComponent(cookie.split("=")[1]) : null;
};


export const AuthProvider = ({ children }) => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [user, setUser] = useState(null);
    const [csrfToken, setCsrfToken] = useState('');

    useEffect(()=>{
        const token = getCsrfTokenFromCookie();
        if(token) {
            setCsrfToken(token);
            axios.defaults.headers.common["X-XSRF-TOKEN"] = token;
        }
    }, []);

    useEffect(() => {
        console.log("Updated CSRF Token:", csrfToken);
    }, [csrfToken]);

    // ✅ Login function
    const login = async (userData) => {
        try {
            console.log("Login called with user data:", userData);
            console.log('Current token is- ', csrfToken);
            const response = await axios.post(`${config.API_BASE_URL}/auth/login`, userData, {
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json',
                    'X-XSRF-TOKEN': csrfToken
                }
            });

            console.log("Login successful:", response.data);
            setIsAuthenticated(true);
            setUser(userData);
        } catch (err) {
            console.error('❌ Login Failed!', err);
            console.error('🔍 Full Axios Error:', err.toJSON ? err.toJSON() : err);
        }
    };

    // ✅ Logout function
    const logout = async () => {
        try {
            await axios.post(`${config.API_BASE_URL}/auth/logout`, {}, { withCredentials: true });
            setIsAuthenticated(false);
            setUser(null);
            const token = await fetchCsrfToken();
            if (token) setCsrfToken(token);
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

export { AuthContext};
export default AuthProvider;
