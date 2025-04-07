import { useEffect, useState } from "react";

const ThemeFadeOverlay = () => {
  const [visible, setVisible] = useState(false);

  useEffect(() => {
    const handleThemeToggle = () => {
      setVisible(true);
      setTimeout(() => setVisible(false), 400); // Matches CSS duration
    };

    window.addEventListener("theme-toggle", handleThemeToggle);
    return () => window.removeEventListener("theme-toggle", handleThemeToggle);
  }, []);

  return visible ? (
    <div className="fixed inset-0 z-[9999] bg-white dark:bg-[#0c0c0c] transition-opacity duration-300 opacity-0 animate-fade" />
  ) : null;
};

export default ThemeFadeOverlay;

