import '../styles/AuthPage.css';
import React, { useState } from 'react';
import axios from 'axios';
import config from '../config';  // Import the config file

const AuthPage = () => {
  const [isLogin, setIsLogin] = useState(true);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage('');  // Clear previous errors
    const endpoint = isLogin ? '/auth/login' : '/auth/signup';

    try {
      const response = await axios.post(`${config.API_BASE_URL}${endpoint}`, { email, password });
      console.log(`${isLogin ? 'Login' : 'Signup'} successful:`, response.data);
    } catch (error) {
      if (error.response) {
        // Handle specific error codes from the backend
        if (error.response.status === 409) {
          setErrorMessage('Invalid username or password!');
        } else if (error.response.status === 400) {
          setErrorMessage('Bad request. Please check your inputs.');
        } else if (error.response.status === 500) {
          setErrorMessage('Internal server error. Please try again later.');
        } else {
          setErrorMessage('An unexpected error occurred. Please try again.');
        }
      } else {
        setErrorMessage('Unable to reach the server. Check your connection.');
      }
      console.error(`${isLogin ? 'Login' : 'Signup'} failed:`, error);
    }
  };

  return (
    <div style={{ textAlign: 'center', padding: '50px' }}>
      <h2>{isLogin ? 'Login' : 'Signup'}</h2>
      <form onSubmit={handleSubmit}>
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <br />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <br />
        {errorMessage && <p style={{ color: 'red' }}>{errorMessage}</p>}
        <button type="submit">{isLogin ? 'Login' : 'Signup'}</button>
      </form>
      <br />
      <button onClick={() => setIsLogin(!isLogin)}>
        {isLogin ? 'Switch to Signup' : 'Switch to Login'}
      </button>
    </div>
  );
};

export default AuthPage;
