// src/components/Header.jsx
import ThemeToggle from "./ThemeToggle";

const Header = () => {
  return (
    <header className="w-full px-6 py-4 flex items-center justify-between border-b border-gray-200 dark:border-zinc-800 bg-white dark:bg-zinc-900 transition-colors duration-300">
      <h1 className="text-xl font-semibold text-gray-800 dark:text-white">
        UnCypher Dashboard
      </h1>
      <ThemeToggle />
    </header>
  );
};

export default Header;


