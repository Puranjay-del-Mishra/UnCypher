// src/pages/AuthPage.jsx
import { useContext, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Mail, Lock } from "lucide-react";

import AuthContext from "../context/AuthContext";
import config from "../config";
import apiBare from "../utils/apiBare";
import PasswordInput from "../components/PasswordInput";
import TopRightUI from "../components/TopRightUI";

export default function AuthPage() {
  const { login } = useContext(AuthContext);
  const [isLogin, setIsLogin] = useState(true);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [passwordScore, setPasswordScore] = useState(0);
  const [passwordBreached, setPasswordBreached] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    if (!email || !password) {
      setError("Please enter email and password.");
      setLoading(false);
      return;
    }

    if (!isLogin && (passwordScore < 3 || passwordBreached)) {
      setError("Please choose a stronger password.");
      setLoading(false);
      return;
    }

    try {
      if (isLogin) {
        await login({ email, password });
      } else {
        await apiBare.post(`${config.API_BASE_URL}/auth/signup`, { email, password });
        await login({ email, password }); // auto-login after signup
      }
      navigate("/app");
    } catch (err) {
      console.error("❌ Auth Error:", err);
      setError("Invalid credentials or signup failed.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100 dark:bg-[#0c0c0c] px-4">
      <TopRightUI />
      <div className="bg-white dark:bg-zinc-900 p-8 rounded-2xl shadow-xl w-full max-w-md space-y-6">
        <div className="text-center text-2xl font-bold text-blue-600 dark:text-blue-400">
          UnCypher
        </div>

        <h2 className="text-center text-xl font-semibold text-gray-800 dark:text-white">
          {isLogin ? "Welcome Back 👋" : "Create your UnCypher account"}
        </h2>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="relative">
            <Mail className="absolute left-3 top-3.5 text-gray-400" size={18} />
            <input
              type="email"
              placeholder="Email"
              className="w-full pl-10 pr-3 py-2 rounded-md bg-gray-100 dark:bg-zinc-800 text-gray-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          {isLogin ? (
            <div className="relative">
              <Lock className="absolute left-3 top-3.5 text-gray-400" size={18} />
              <input
                type="password"
                placeholder="Password"
                className="w-full pl-10 pr-3 py-2 rounded-md bg-gray-100 dark:bg-zinc-800 text-gray-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>
          ) : (
            <PasswordInput
              onChange={(val, score, breached) => {
                setPassword(val);
                setPasswordScore(score);
                setPasswordBreached(breached);
              }}
            />
          )}

          {error && <p className="text-red-500 text-sm text-center">{error}</p>}

          <button
            type="submit"
            disabled={loading}
            className={`w-full py-2 font-semibold rounded-md transition ${
              loading
                ? "bg-gray-400 text-white cursor-not-allowed"
                : "bg-black dark:bg-white text-white dark:text-black hover:opacity-90"
            }`}
          >
            {loading ? (isLogin ? "🔍 Logging in..." : "🔍 Signing up...") : isLogin ? "Login" : "Sign Up"}
          </button>
        </form>

        <div className="text-center text-sm text-gray-600 dark:text-gray-400">
          {isLogin ? (
            <>
              Don’t have an account?{" "}
              <button
                onClick={() => setIsLogin(false)}
                className="text-blue-600 dark:text-blue-400 hover:underline"
              >
                Sign up
              </button>
            </>
          ) : (
            <>
              Already registered?{" "}
              <button
                onClick={() => setIsLogin(true)}
                className="text-blue-600 dark:text-blue-400 hover:underline"
              >
                Login
              </button>
            </>
          )}
        </div>
      </div>
    </div>
  );
}




