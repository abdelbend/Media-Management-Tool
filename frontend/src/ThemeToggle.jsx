import React, { useContext } from "react";
import { ThemeContext } from "./ThemeContext";
import { Sun, Moon } from "lucide-react";
import IconButton from "@mui/material/IconButton";

export default function ThemeToggle() {
  const { mode, toggleTheme } = useContext(ThemeContext);

  return (
    <IconButton onClick={toggleTheme} aria-label="toggle theme" color="inherit">
      {mode === "dark" ? (
        <Sun className="text-yellow-500" size={20} />
      ) : (
        <Moon className="text-gray-800" size={20} />
      )}
    </IconButton>
  );
}
