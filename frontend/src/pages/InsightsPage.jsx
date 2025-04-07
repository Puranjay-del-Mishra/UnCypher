import { Outlet } from "react-router-dom";
import Header from "../components/Header";
import Sidebar from "../components/Sidebar";

export default function AppLayout() {
  return (
    <div className="flex min-h-screen">
      <Sidebar />
      <div className="flex-1 flex flex-col">
        <Header />
        <main className="flex-1 p-6 bg-gray-50 dark:bg-zinc-900 overflow-y-auto">
          <Outlet /> {/* Will render nested routes like /dashboard */}
        </main>
      </div>
    </div>
  );
}
