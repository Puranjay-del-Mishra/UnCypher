import React, { useState, useContext } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import config from '../config';
import {AuthContext} from '../context/AuthContext';
import '../styles/AuthPage.css';
import logo from '../assets/uncypher_logo.png'; // ✅ Ensure logo exists

const AuthPage = () => {
  const { login, csrfToken} = useContext(AuthContext); // ✅ Remove isAuthenticated (not needed)
  const [isLogin, setIsLogin] = useState(true);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage('');
    const endpoint = isLogin ? '/auth/login' : '/auth/signup';

    try {
      console.log('Login event happened, csrf token for the same is - ', csrfToken);
      const response = await axios.post(`${config.API_BASE_URL}${endpoint}`, { email, password },
          { withCredentials: true,
          headers: {'X-XSRF-TOKEN': csrfToken, // ✅ explicitly include CSRF token header
      }});
      console.log('The user data is - ', response.config.data);
      await login(response.config.data); // ✅ Pass only the `user` object
      navigate('/dashboard');
    } catch (error) {
      console.error('❌ Axios error:', error);
      setErrorMessage(
        error.response?.status === 409 ? 'Invalid username or password!' :
        error.response?.status === 400 ? 'Bad request. Please check your inputs.' :
        error.response?.status === 500 ? 'Internal server error. Please try again later.' :
        'An unexpected error occurred. Please try again.'
      );
    }
  };

  return (
    <div className="auth-container">
      <h2>{isLogin ? 'Login' : 'Signup'}</h2>
      <form onSubmit={handleSubmit}>
        <input type="email" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        <input type="password" placeholder="Password" value={password} onChange={(e) => setPassword(e.target.value)} required />
        {errorMessage && <p className="error-message">{errorMessage}</p>}
        <button type="submit">{isLogin ? 'Login' : 'Signup'}</button>
      </form>
      <button className="switch-button" onClick={() => setIsLogin(!isLogin)}>
        {isLogin ? 'Switch to Signup' : 'Switch to Login'}
      </button>
    </div>
  );
};

export default AuthPage;