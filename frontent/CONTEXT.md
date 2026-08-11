# Frontend UI Layer

Vocabulary for how this codebase consumes UI component libraries, established during the PrimeNG → spartan-ng migration (see [ADR-0001](./docs/adr/0001-incremental-primeng-to-spartan-migration.md)).

## Language

**Brain primitive**:
A headless, unstyled Angular CDK-based building block from spartan-ng's Brain layer, installed as a normal npm dependency. Provides behavior and accessibility logic only; carries no Tailwind styling.
_Avoid_: headless component, CDK primitive

**Helm component**:
A styled Tailwind component generated directly into this repo by the spartan-ng CLI (Brain + styling), not installed as an opaque npm package. Once generated, it is code this project owns and edits directly — updates come from re-running the CLI and diffing, not `npm update`.
_Avoid_: spartan component, UI library component (when precision matters — use this term specifically for the copied-in, owned kind)
