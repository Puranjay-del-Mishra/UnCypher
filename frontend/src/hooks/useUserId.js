import { useContext } from "react";
import AuthContext from "../context/AuthContext";

export const useUserId = () => {
    const { user } = useContext(AuthContext);
    return user?.sub || null; // sub = username / email / UUID
};
