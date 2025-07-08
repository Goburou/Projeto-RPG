// tailwind.config.js
/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}", // Esta linha diz ao Tailwind para escanear todos os arquivos .js, .ts, .jsx e .tsx dentro da pasta src/
  ],
  theme: {
    extend: {},
  },
  plugins: [],
}