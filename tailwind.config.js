/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './src/main/resources/templates/**/*.html',
    './src/main/resources/static/js/**/*.js'
  ],
  theme: {
    extend: {
      colors: {
        purple: {
          100: '#e0e7ff',
          200: '#c7d2fe',
          300: '#a5b4fc',
          400: '#818cf8',
          500: '#6366f1',
          600: '#4f46e5',
          700: '#4338ca',
          800: '#3730a3',
          900: '#312e81',
        },
        brand: {
          50 : '#f4f9ff',
          100: '#e0edff',
          200: '#c7dffe',
          300: '#a5cbfc',
          400: '#81b5f8',
          500: '#63a1f1',
          600: '#468be5',
          700: '#3878ca',
          800: '#3062a3',
          900: '#2e5281',
        }
      }
    },
  },
  plugins: [
    require('daisyui'),
  ],
  daisyui: {
    themes: ['socialApp', 'dark', 'cupcake'],
  },
}
