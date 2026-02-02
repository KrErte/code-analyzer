/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        'code-bg': '#1e1e1e',
        'code-text': '#d4d4d4',
        'critical': '#ef4444',
        'warning': '#f59e0b',
        'suggestion': '#3b82f6',
      },
      fontFamily: {
        'mono': ['JetBrains Mono', 'Fira Code', 'Monaco', 'Consolas', 'monospace'],
      }
    },
  },
  plugins: [],
}
