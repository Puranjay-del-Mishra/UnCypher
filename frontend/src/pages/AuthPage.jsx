import React, { useState, useContext } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import config from "../config";
import AuthContext from "../context/AuthContext";
import "../styles/AuthPage.css";
import logo from "../assets/uncypher_logo.png"; // ✅ Ensure logo exists
import getCookie from "../utils/cookie"; // or wherever you defined it
import {getCsrfToken, resetCsrf} from "../utils/api.js";
import apiBare from "../utils/apiBare";

const AuthPage = () => {
  const { login } = useContext(AuthContext);
  const [isLogin, setIsLogin] = useState(true);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage("");
    const endpoint = isLogin ? "/auth/login" : "/auth/signup";

    try {

      console.log("📩 Sending login request with email:", email);

//       console.log(response.config.data)
      await login({ email, password }); // ✅ Correctly calling login function

      console.log("✅ Login successful:");

      navigate("/dashboard"); // ✅ Redirect after successful login

    } catch (error) {
      console.error("❌ Axios error:", error);
      setErrorMessage(
        error.response?.status === 409
          ? "Invalid username or password!"
          : error.response?.status === 400
          ? "Bad request. Please check your inputs."
          : error.response?.status === 500
          ? "Internal server error. Please try again later."
          : "An unexpected error occurred. Please try again."
      );
    }
  };

  return (
    <div className="auth-container">
      <img src={logo} alt="UnCypher Logo" className="auth-logo" />
      <h2>{isLogin ? "Login" : "Signup"}</h2>
      <form onSubmit={handleSubmit}>
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        {errorMessage && <p className="error-message">{errorMessage}</p>}
        <button type="submit">{isLogin ? "Login" : "Signup"}</button>
      </form>
      <button className="switch-button" onClick={() => setIsLogin(!isLogin)}>
        {isLogin ? "Switch to Signup" : "Switch to Login"}
      </button>
    </div>
  );
};

export default AuthPage;
