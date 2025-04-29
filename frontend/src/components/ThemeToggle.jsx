import { useContext } from "react";
import { Sun, Moon } from "lucide-react";
import ThemeContext from "../context/ThemeContext";

const ThemeToggle = () => {
  const { darkMode, setDarkMode } = useContext(ThemeContext);

  const handleToggle = () => {
    const newMode = !darkMode;
    setDarkMode(newMode);

    // Persist theme preference
    localStorage.setItem("theme", newMode ? "dark" : "light");
    document.documentElement.classList.toggle("dark", newMode);

    // 🌀 Dispatch fade effect
    window.dispatchEvent(new Event("theme-toggle"));
  };

  return (
    <button
      onClick={handleToggle}
      className="rounded-full p-2 hover:bg-gray-200 dark:hover:bg-zinc-800 transition"
      aria-label="Toggle Theme"
    >
      {darkMode ? (
        <Sun className="w-5 h-5 text-yellow-400" />
      ) : (
        <Moon className="w-5 h-5 text-blue-400" />
      )}
    </button>
  );
};

export default ThemeToggle;

