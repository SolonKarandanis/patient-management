# Facade services wrap spartan's toast/dialog primitives

PrimeNG's `MessageService`/`ConfirmationService` are plain injectable singletons, callable from anywhere — including non-component files like `role.guard.ts` and `error.service.ts` — because they push into a global overlay regardless of what's currently rendered. spartan-ng's toast (Sonner) is a `toast()` function backed by a global queue rendered by a single root-mounted `Toaster`-style component, and its Alert Dialog is CDK-overlay/component-based; neither is a classic injectable Angular service by default.

We decided to wrap them in our own `ToastService` and `ConfirmService` (`providedIn: 'root'`, following the `UserService` reference-implementation convention from `docs/architecture-state-management.md`), each backed by spartan's primitives mounted once at the app root. Every existing call site (`role.guard.ts`, `error.service.ts`, `util.service.ts`, `search-buttons`, `saved-searches`) swaps which service it injects — no call-site refactor required.

Named `ToastService` rather than `NotificationService`: `core/services/notification.service.ts` already exists for STOMP/Artemis real-time push notifications (see `AUTH.md`/`CLAUDE.md`'s real-time architecture), an unrelated concern. Reusing that name for the toast facade would collide with it.

The alternative — routing those call sites through a signal-based global notification store instead, rendered declaratively by a root component — is more idiomatic to the zoneless, signals-first architecture already in place, but is a separate refactor from the PrimeNG swap. We deferred it to keep this migration's blast radius to "replace the library underneath."
