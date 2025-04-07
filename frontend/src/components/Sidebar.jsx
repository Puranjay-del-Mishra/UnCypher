// src/components/Sidebar.jsx
import { NavLink } from "react-router-dom";

const Sidebar = () => {
  const navItemStyle = ({ isActive }) =>
    `flex items-center gap-2 px-4 py-2 rounded-lg font-medium transition-all ${
      isActive
        ? "bg-primary text-white"
        : "text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-zinc-800"
    }`;

  return (
    <aside className="hidden md:block w-60 min-h-screen bg-white dark:bg-zinc-950 border-r border-gray-200 dark:border-zinc-800 p-4">
      <nav className="flex flex-col gap-2">
        <NavLink to="/app/dashboard" className={navItemStyle}>
          📊 Dashboard
        </NavLink>
        <NavLink to="/app/insights" className={navItemStyle}>
          🧠 Insights
        </NavLink>
        <NavLink to="/app/settings" className={navItemStyle}>
          ⚙️ Settings
        </NavLink>
        <NavLink
          to="/"
          className="mt-6 px-4 py-2 text-sm text-red-600 hover:underline transition"
        >
          🚪 Logout
        </NavLink>
      </nav>
    </aside>
  );
};

export default Sidebar;

