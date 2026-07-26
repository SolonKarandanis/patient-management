# Signal Store

## Location of Stores

- Place new Signal Stores at the feature level whenever possible.
- If a store is needed by additional features, move it down to a lower level.
- If a store is needed across different domains, consult the user before moving it to the shared area.

## Granularity of Stores

- A store MUST manage exactly one of the following responsibilities — never a mix:
  1. **Search/list state** of one entity type (the collection, filters, paging).
  2. **Detail/edit state** of a single entity (selected/edited entity, create/update/delete).
  3. **A piece of UI state.**
  4. **Lookup data** (e.g., dropdown values). Bundle these in one store per feature
     named `<Feature>LookupStore`.
  5. **Read-only aggregate/dashboard state** — derived, non-paginated reporting data
     with no single edited entity and no dropdown/lookup semantics (e.g. `AnalyticsStore`).
     Named `<Feature>Store` — no further suffix, since it's neither a paginated list nor
     a lookup value set.

- Naming MUST make the responsibility explicit (examples use arbitrary entities from
  arbitrary domains; the rule applies to every entity in every domain):
  - Search/list store: `<Entity>SearchStore` (e.g., `CustomerSearchStore`,
    `InvoiceSearchStore`).
  - Detail/edit store: `<Entity>DetailStore` (e.g., `OrderDetailStore`,
    `EmployeeDetailStore`).
  - These map 1:1 to the smart-component suffixes `Search` and `Detail`/`Edit`.

- A "manage" / CRUD feature is NOT one store. Split it:
  - the list/overview belongs to the `<Entity>SearchStore`;
  - selecting, creating, updating and deleting a single record belongs to the
    `<Entity>DetailStore`.
  - Concretely: `editId`, `selectedEntity`, `create`, `update`, `remove`, `startEdit`
    and `cancelEdit` MUST live in the detail/edit store, never in the search/list store.

- Never perform data access directly within a store; delegate it to a data access service instead.

### Exception: core cross-cutting infrastructure stores

- App-wide, singleton infrastructure state that isn't a domain feature at all — e.g.
  `AuthStore` (the current session) — is exempt from the granularity categories and
  naming rules above, the same way files inside an `ai` layer are exempt from the
  smart/dumb access rules below.
- This exemption is about what the state *is*, not where the file lives: living under
  `core/store/` is not by itself a qualifying signal. `ChatbotUiStore`/`ChatbotDetailStore`
  also live under `core/store/` (chat has no `protected/<domain>/` folder of its own) but
  still follow the normal UI-state/Detail categories above — they're a feature, just one
  without a dedicated domain folder. The exemption applies only when the state has no
  natural entity or feature boundary at all — `AuthStore` is the only current example.
- This exemption is narrow. It is not a way to avoid splitting a store that is actually a
  domain feature in disguise.

### Self-check before adding state to a store

- Does the store already hold list state AND a selected/edited record? → split it.
- Does the store name end in `SearchStore`, `DetailStore`, `LookupStore`, or — for the
  read-only aggregate case — plain `<Feature>Store`? If none of these, and it isn't a
  core infrastructure store per the exception above, justify why.
- Could a list view and an edit view import this store independently? They should
  import _different_ stores.

## Store Dependencies

- A store MUST NOT depend on another store. Combining state from several stores is the
  job of a service, never of a store itself.

## Services

- When a feature needs to read and combine state from several stores (and delegate writes
  back to them), introduce a service instead of letting a store depend on other stores.
- A service is a plain `@Injectable({ providedIn: 'root' })` service class — NOT a
  Signal Store. Use the suffix `Service` and the file suffix `-service.ts`
  (e.g., `UserService` in `user-service.ts`).
- A service MAY inject several stores; it typically exposes `computed` views derived
  from them and forwards write actions to the underlying stores.
- Follow `UserService` as the reference implementation.

## Structure of Stores

- Give every store's state its own `<name>.state.ts` file exporting the state type and an
  `initial<Name>State` constant; import both into the store. Omit this file entirely for a
  store that holds no state of its own beyond call state (e.g. a store whose only job is a
  single write operation, such as `I18nResourceDetailStore`).
- Compose stores from these building blocks, in this order:
  1. `withState<...>(initial...)` — the store's own state (skip if none, see above).
  2. `withCallState()` — shared feature (`core/store/features/call-state.feature.ts`)
     adding `loading`, `loaded`, `error`, `status` computed signals. Use on every store that
     talks to a data access service.
  3. `withSearchState()` — shared feature (`core/store/features/search-state.feature.ts`)
     adding `criteriaCollapsed`, `hasSearched`, `tableLoading`. Search/list stores only.
  4. `withProps(() => ({ ... }))` — inject the repository/data access service and any other
     services (`UtilService`, `TranslateService`, etc.) the store's methods need.
  5. `withComputed(...)` (optional) — derived signals over the store's own state.
  6. `withMethods((state) => ({ ... }))` — synchronous `patchState` setters (e.g.
     `setLoadingState`, `setSelectedUser`).
  7. A second `withMethods((state) => { const { repo, ... } = state; return ({ ... }) })` —
     the async operations, each an `rxMethod` that calls the repository and handles the
     result with `tapResponse({ next, error })`, delegating to the setters from block 6.
- Follow `UserSearchStore` / `UserDetailStore` (`protected/user/data/store/`) as the
  reference implementation for a Search/Detail pair, and `I18nLookupStore`
  (`protected/i18n/data/store/`) for a Lookup store.
- Exception: a store that only wraps read-only, non-paginated server data (e.g.
  `AnalyticsStore`) may use Angular's native `httpResource()` — exposed by the data access
  service and held via `withProps` — instead of `rxMethod`/`tapResponse`, computing
  `loading`/`error` from the resource's own signals. Skip `withCallState()` in that case; it
  would be redundant.

## Smart and Dumb Components and Stores

- Only smart components are permitted to use services.
- Only services are permitted to use stores.
- Smart components use the following suffixes: `Page`, `Search`, `Detail`, `Edit`,
  `Overview` (e.g., `FlightSearch`, `UserDetails`).
- Components obtain data only from a service that combines several
  stores — never directly from a data access service.
- Exception (locality): a dumb component MAY use a store that is co-located in the same
  folder or in a child folder of it.
- Exception (ai): files inside an `ai` layer (any `ai/` folder) are exempt from these
  access restrictions and may access stores and data access services directly.
