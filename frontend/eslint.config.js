import js from '@eslint/js';
import globals from 'globals';

export default [
  { ignores: ['dist/**'] },
  {
    files: ['**/*.{js,jsx}'],
    languageOptions: {
      ecmaVersion: 'latest',
      sourceType: 'module',
      // Without this, ESLint cannot parse JSX and `npm run lint` fails on every .jsx file.
      parserOptions: { ecmaFeatures: { jsx: true } },
      globals: { ...globals.browser }
    },
    rules: {
      ...js.configs.recommended.rules,
      // Capitalised names are React components used in JSX, which core ESLint does not count as "used".
      'no-unused-vars': ['warn', { argsIgnorePattern: '^([A-Z]|_)', varsIgnorePattern: '^([A-Z]|_)' }]
    }
  }
];
