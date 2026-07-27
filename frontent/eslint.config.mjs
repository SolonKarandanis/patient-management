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
  },
  sheriffConfigs.all,
);
