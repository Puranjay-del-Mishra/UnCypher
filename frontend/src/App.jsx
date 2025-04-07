import { Navigate, BrowserRouter as Router, Routes, Route } from "react-router-dom";
import AuthPage from "./pages/AuthPage.jsx";
import AppLayout from "./pages/AppLayout.jsx";  // NEW shared layout
import Dashboard from "./pages/Dashboard.jsx";
import InsightsPage from "./pages/InsightsPage.jsx";
import SettingsPage from "./pages/SettingsPage.jsx";
import { ThemeProvider } from "./context/ThemeContext";
import { AuthProvider } from "./context/AuthContext";
import PrivateRoute from "./routes/PrivateRoute";
import ThemeFadeOverlay from "./components/ThemeFadeOverlay";

import "./App.css"; // Optional legacy styles

function App() {
  return (
    <ThemeProvider>
      <AuthProvider>
        <Router>
          <ThemeFadeOverlay />
          <Routes>
            {/* Public Route */}
            <Route path="/" element={<AuthPage />} />

            {/* Protected App Shell */}
            <Route
              path="/app"
              element={
                <PrivateRoute>
                  <AppLayout />
                </PrivateRoute>
              }
            >
              {/* ✅ Default: /app → /app/dashboard */}
              <Route index element={<Navigate to="dashboard" replace />} />

              {/* ✅ Sub-pages */}
              <Route path="dashboard" element={<Dashboard />} />
              <Route path="insights" element={<InsightsPage />} />
              <Route path="settings" element={<SettingsPage />} />
            </Route>
          </Routes>
        </Router>
      </AuthProvider>
    </ThemeProvider>
  );

}

export default App;

