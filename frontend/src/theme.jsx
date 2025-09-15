import { createTheme } from "@mui/material/styles";

export const getTheme = (mode) =>
  createTheme({
    palette: {
      mode,
      ...(mode === "dark"
        ? {
            background: {
              default: "#121212",
              paper: "#1D1D1D",
            },
          }
        : {
            background: {
              default: "#f5f5f5",
              paper: "#ffffff",
            },
          }),
    },
  });
