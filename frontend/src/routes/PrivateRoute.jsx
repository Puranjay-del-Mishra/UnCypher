import { useContext } from "react";
import { Navigate } from "react-router-dom";
import AuthContext from "../context/AuthContext";

const PrivateRoute = ({ children }) => {
  const { isAuthenticated } = useContext(AuthContext);

  // While auth is still undefined (initial load), wait
  if (isAuthenticated === null) return null;

  return isAuthenticated ? children : <Navigate to="/" />;
};

export default PrivateRoute;
