// tailwind.config.js
module.exports = {
  darkMode: 'class', // ← IMPORTANT!
  content: ['./index.html', './src/**/*.{js,jsx,ts,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: '#6366f1', // optional custom color
      },
    },
  },
  plugins: [],
};
