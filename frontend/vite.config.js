import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// Прокси нужен только для локальной разработки (npm run dev), чтобы
// не ловить CORS, пока API_BASE_URL в public/config.js указывает на localhost.
// В проде config.js обычно указывает на боевой домен API напрямую, прокси не нужен.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
});
