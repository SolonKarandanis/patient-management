import { sameTag, SheriffConfig } from '@softarc/sheriff-core';

export const config: SheriffConfig = {
  enableBarrelLess: true,
  modules: {
    'src/app/protected/<domain>/data': ['domain:<domain>', 'type:data'],
    'src/app/protected/<domain>': ['domain:<domain>', 'type:feature'],
    'src/app/protected': ['shell'],
    'src/app/auth': ['domain:auth'],
    'src/app/core': ['core'],
    'src/app/shared/components': ['shared', 'type:ui'],
    'src/app/shared/components/ui/<component>/src': ['shared', 'type:ui'],
    'src/app/shared/directives': ['shared', 'type:ui'],
    'src/app/shared/animations': ['shared', 'type:ui'],
    'src/app/shared/utils': ['shared', 'type:util'],
    'src/app/shared/abstract': ['shared', 'type:util'],
    'src/app/components': ['shared', 'type:ui'],
    'src/app/errors': ['shell'],
    'src/app/unauthorized': ['shell'],
  },
  depRules: {
    root: ['core', 'shared', 'shell', 'domain:*'],
    shell: ['core', 'shared', 'domain:*'],
    core: ['shared', 'root'],
    shared: ['core', 'shared'],
    'domain:*': [sameTag, 'core', 'shared', 'root'],
    'domain:auth': [sameTag, 'core', 'shared', 'root', 'domain:user'], // approved exception: registration reuses UserService via protected/user's index.ts barrel only
    'type:feature': ['type:ui', 'type:data', 'type:util', 'core', 'shared', 'root'],
    'type:data': ['type:feature', 'type:util', 'core', 'shared', 'root'], // relaxed both ways: per-domain forms.ts sits at the domain root (type:feature) but is a data-contract file used by both layers
    'type:ui': ['type:util', 'core', 'shared', 'root'],
    'type:util': ['core', 'shared', 'root'],
  },
};
