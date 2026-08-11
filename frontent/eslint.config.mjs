import tseslint from 'typescript-eslint';
import { configs as sheriffConfigs } from '@softarc/eslint-plugin-sheriff';

export default tseslint.config(
  {
    ignores: ['.angular/**', 'dist/**', 'cypress/**', 'testing/**'],
  },
  {
    files: ['**/*.ts'],
    languageOptions: {
      parser: tseslint.parser,
      parserOptions: {
        sourceType: 'module',
      },
    },
    plugins: {
      '@typescript-eslint': tseslint.plugin,
      // Stub namespace only: generated Helm vendor code carries `eslint-disable`
      // comments for @angular-eslint rules, but this project doesn't otherwise
      // use angular-eslint. Registering a no-op rule definition lets those
      // comments resolve instead of hard-erroring on an unknown rule.
      '@angular-eslint': {rules: {'no-output-native': {create: () => ({})}}},
    },
  },
  sheriffConfigs.all,
);
