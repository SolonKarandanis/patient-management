# `results-table` rebuilt on spartan Data Table in manual mode

`results-table` wraps PrimeNG's `p-table` with server-side lazy loading (`TableLazyLoadEvent`): the store owns paging/sorting/filtering, and the table only reports interaction events, feeding `withSearchState()`'s `tableLoading` signal. Client-side pagination — loading a full result set into the browser and paging over it locally — is ruled out as an option for this component, regardless of implementation, due to the cost of transferring and holding full result sets client-side.

spartan's Data Table is a thin wrapper around TanStack Table. Its documented examples (`getPaginationRowModel()`, `getSortedRowModel()`, `getFilteredRowModel()`) are client-side row-model transformations — the opposite of what we need. We decided to build on it anyway, configured in TanStack's "manual" mode (`manualPagination`/`manualSorting`/`manualFiltering`), wiring its `onPaginationChange`/`onSortingChange`/`onColumnFiltersChange` callbacks to the existing store's data-fetching methods, preserving continuity with the current `tableLoading` contract. The client-side row-model APIs (`getPaginationRowModel()` etc.) must never be enabled on this table.

## Consequences

- We're taking a dependency on TanStack Table's API surface mostly for its column-definition types and render helpers, not its primary client-side-transformation feature — accepted as the cost of staying consistent with the rest of the spartan-ng adoption rather than hand-rolling a table primitive.
- Any future reviewer who sees `getPaginationRowModel()`/`getSortedRowModel()`/`getFilteredRowModel()` added to this table should treat it as a regression toward client-side pagination, not a valid enhancement.

## Implementation note: v9's "explicit features" API

`@tanstack/angular-table`'s `latest` npm tag resolved to `9.1.2` at implementation time, wrapping `@tanstack/table-core@9.1.2` — a ground-up rewrite from the v8 API this ADR was written against. There is no `getCoreRowModel()`/`getPaginationRowModel()`/`getSortedRowModel()`/`getFilteredRowModel()` table-options surface in v9 at all: features (state, column/table APIs) and row-model factories are both registered explicitly via `tableFeatures({...})`, and only what's registered exists at runtime.

The ban above translates literally to: `tableFeatures({...})` registers `rowSortingFeature`, `rowPaginationFeature`, `rowSelectionFeature` (state and column/table APIs only) and never registers the `sortedRowModel` / `paginatedRowModel` / `filteredRowModel` row-model-factory slots those features also expose. If a future change adds any of those three slots to the `tableFeatures({...})` call in `results-table.component.ts`, treat it exactly as this ADR's original text intended: a regression toward client-side pagination.

The global text-search box (client-side substring filter over whatever page is currently loaded, never server-side, pre-dating this rebuild) is implemented as a plain `computed()` signal filtering `tableItems()` before it's handed to the table's `data` option — it never touches TanStack filtering state or a row-model slot.
