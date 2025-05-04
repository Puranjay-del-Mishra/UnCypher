import { Outlet } from "react-router-dom";
import Header from "../components/Header";
import Sidebar from "../components/Sidebar";
import ThemeFadeOverlay from "../components/ThemeFadeOverlay";
import MapboxMap from "../components/mapbox/MapboxMap";
import GlobalLayers from "../components/mapbox/GlobalLayers";

const AppLayout = () => {
  return (
    <>
      <ThemeFadeOverlay />
      <div className="flex min-h-screen bg-gray-50 dark:bg-[#0c0c0c] text-black dark:text-white transition-colors duration-300">
        <Sidebar />
        <div className="flex-1 flex flex-col relative">
          <MapboxMap initialCenter={[0, 0]} zoom={2}>
            <GlobalLayers />
          </MapboxMap>

          <div className="relative z-10">
            <Header />
            <main className="flex-1 p-6 overflow-y-auto">
              <Outlet />
            </main>
          </div>
        </div>
      </div>
    </>
  );
};

export default AppLayout;



