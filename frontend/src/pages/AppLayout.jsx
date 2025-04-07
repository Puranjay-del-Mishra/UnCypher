// src/components/Layout.jsx
import { Outlet } from "react-router-dom";
import Header from "../components/Header";
import Sidebar from "../components/Sidebar";
import ThemeFadeOverlay from "../components/ThemeFadeOverlay"; // optional, if using transition

const AppLayout = () => {
  return (
    <>
      <ThemeFadeOverlay />
      <div className="flex min-h-screen bg-gray-50 dark:bg-[#0c0c0c] text-black dark:text-white transition-colors duration-300">
        <Sidebar />
        <div className="flex-1 flex flex-col">
          <Header />
          <main className="flex-1 p-6 overflow-y-auto">
            <Outlet />
          </main>
        </div>
      </div>
    </>
  );
};

export default AppLayout;

