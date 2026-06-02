# Auth Flow — OAuth2 / Keycloak Mode

This document covers the end-to-end OAuth2 authentication flow when `auth.mode=oauth2`.
The system also supports a legacy custom-JWT mode (`auth.mode=jwt`); that mode is not covered here.

---

## Configuration

### auth-service (`application.properties`)

```properties
auth.mode=oauth2
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8090/realms/patient-management
hazelcast.session.management.enabled=false
```

Spring uses `issuer-uri` to auto-discover the Keycloak JWKS endpoint and validate incoming JWTs.

### Frontend (`environment.ts`)

```ts
keycloakIssuer:   'http://localhost:8090/realms/patient-management'
keycloakClientId: 'angular-frontend'
```

---

## Backend — auth-service

### Active security configuration

`OAuth2SecurityConfiguration` is loaded when:

```
hazelcast.session.management.enabled == false  AND  auth.mode == 'oauth2'
```

It replaces `WebSecurityConfiguration` (the JWT-mode chain).

**Filter chain rules (`SecurityFilterChain`):**

| Matcher | Rule |
|---|---|
| `AUTH_WHITELIST` (`/swagger-ui/*`, `/actuator/**`, `/public/**`, etc.) | `permitAll` |
| `POST /login` | `permitAll` |
| `POST /users` | `permitAll` |
| Everything else | `authenticated()` |

Session policy is `STATELESS`. CORS is applied when `cors.enabled=true`.

**`JwtAuthenticationFilter` is disabled** via a `FilterRegistrationBean` registered in this config.
Without this, Spring Boot auto-registers the filter as a servlet filter and it tries to parse the Keycloak RS256 token with the HMAC secret key, causing a 500.

### JWT validation

Spring's `oauth2ResourceServer` validates the Keycloak access token:

1. On first request, fetches the JWKS from `http://localhost:8090/realms/patient-management/protocol/openid-connect/certs` and caches the public keys.
2. Verifies the RS256 signature, expiry, and issuer of every incoming Bearer token.

### Role extraction (`JwtAuthenticationConverter`)

Keycloak places realm roles in a nested claim:

```json
{
  "realm_access": {
    "roles": ["ROLE_ADMIN", "ROLE_DOCTOR", ...]
  }
}
```

The standard `JwtGrantedAuthoritiesConverter` does not support nested paths, so a custom lambda extracts the roles:

```java
converter.setJwtGrantedAuthoritiesConverter(jwt -> {
    Map<String, Object> realmAccess = jwt.getClaim("realm_access");
    Collection<String> roles = (Collection<String>) realmAccess.get("roles");
    return roles.stream().<GrantedAuthority>map(SimpleGrantedAuthority::new).toList();
});
```

Roles have no `ROLE_` prefix stripped — they are stored as-is from the token.

### Resolving the current user

Both `UserController` and `CustomMethodSecurityExpressionRoot` need to map the authenticated principal to a `UserJpaEntity` in the local DB. In OAuth2 mode the principal is a `Jwt` object (not a `UserDetailsDTO`), so both use the same pattern:

```java
if (principal instanceof Jwt jwt) {
    return userRepository.findByEmail(jwt.getClaimAsString("email"))...;
} else {
    UserDetailsDTO dto = (UserDetailsDTO) principal;
    return userRepository.findByDomainId(UUID.fromString(dto.getPublicId()))...;
}
```

The `email` claim is the bridge between the Keycloak identity and the local `users` table.

### Method-level authorization

`MethodSecurityConfig` enables `@EnableMethodSecurity` and registers `CustomMethodSecurityExpressionHandler`, which provides the custom SpEL functions used in `@PreAuthorize`:

| Expression | What it checks |
|---|---|
| `isAuthenticated()` | Spring default — valid JWT present |
| `isSystemAdmin()` | `UserUtil.hasRole(currentUser, ROLE_SYSTEM_ADMIN)` |
| `isUserMe(#id)` | `currentUser.domainId == UUID(id)` |
| `hasPermission(op)` | `UserUtil.hasOperation(currentUser, op)` |

`currentUser` is resolved from the DB in the `CustomMethodSecurityExpressionRoot` constructor on every request that hits a `@PreAuthorize`-annotated method.

### `GET /public/config` — auth mode discovery

`PublicApisController` exposes `/public/config` (whitelisted, no auth required).
It returns a `PublicConfiguration` object containing `AUTH_MODE`, read from `auth.mode` in `application.properties` via `ServiceConfigProperties`.
The frontend calls this on startup to decide which auth path to take.

---

## Frontend — Angular

### Bootstrap sequence (`appInitializer`)

`provideAppInitializer(appInitializer)` runs before the app renders:

1. Calls `GET /auth/public/config` (no auth token, whitelisted endpoint).
2. Reads `config.AUTH_MODE` and calls `authModeService.setMode('oauth2')`.
3. Calls `oauthConfigService.initializeAndTryLogin()`.
4. After `initializeAndTryLogin` resolves, `AuthService.initAuth()` runs (called from the root component or equivalent).

### `OAuthConfigService.initializeAndTryLogin()`

Configures `angular-oauth2-oidc` (`OAuthService`) with:

```ts
{
  issuer:               'http://localhost:8090/realms/patient-management',
  redirectUri:          window.location.origin,          // http://localhost:4200
  postLogoutRedirectUri: window.location.origin,
  clientId:             'angular-frontend',
  responseType:         'code',                          // PKCE / Authorization Code Flow
  scope:                'openid profile email',
  clearHashAfterLogin:  true,
}
```

Then calls:
- `loadDiscoveryDocumentAndTryLogin()` — fetches the Keycloak OIDC discovery document and, if an `?code=` query parameter is present in the URL, exchanges it for tokens automatically.
- `setupAutomaticSilentRefresh()` — starts a background timer to refresh the access token before it expires.

### `AuthService.initAuth()`

```ts
if (authModeService.isOAuth2()) {
  if (oauthConfigService.isAuthenticated()) {   // hasValidAccessToken()
    authStore.loadUserAndPermissions();
  } else {
    authStore.logout();
  }
}
```

If the `OAuthService` already holds a valid token (e.g. returning from Keycloak callback or a page refresh with a still-valid token), it immediately loads user data. Otherwise it resets the store to logged-out.

### Reactive redirect to Keycloak

An `effect` in `AuthService` watches `isLoggedIn` and `status`:

```ts
effect(() => {
  if (!isAuthenticated() && status() === 'loaded') {
    oauthConfigService.login();   // OAuthService.initCodeFlow()
  }
});
```

When the store transitions to `loaded` + not logged in (after `authStore.logout()`), the effect fires and redirects the browser to the Keycloak login page.

### `AuthGuard`

Protects routes by checking:

```ts
authService.isAuthenticated()
// = oauthConfigService.isAuthenticated() && authStore.isLoggedIn()
```

Both conditions must be true — the Keycloak token must be valid AND the user data must be loaded in the store.

### `authExpired` interceptor

Attaches the Keycloak access token to outgoing HTTP requests:

```ts
if (request.context.get(AUTHENTICATE_REQUEST)) {   // true by default
  const token = oauthConfigService.getAccessToken();
  request = request.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
}
```

`AUTHENTICATE_REQUEST` is set to `false` only for the login endpoint (`POST /auth/login`).
On a 401 response, the interceptor calls `authService.logout()`.

### `AuthStore.loadUserAndPermissions()`

1. `GET /auth/users/account` — auth-service resolves the current user from the Keycloak `email` claim and returns `UserDTO`. The store calls `setAccount(user)` → `isLoggedIn = true`.
2. `GET /auth/users/{publicId}/permissions` — returns the user's permission names. The store calls `ngxPermissionsService.loadPermissions(permissions)`, enabling `*ngxPermissionsOnly` directives.

---

## Full Login Flow (step by step)

```
Browser                  Angular App               auth-service          Keycloak
  |                          |                          |                    |
  |-- navigate to app ------>|                          |                    |
  |                          |-- GET /auth/public/config->                  |
  |                          |<-- { AUTH_MODE: 'oauth2' }--                 |
  |                          |-- loadDiscoveryDocument() ------------------>|
  |                          |<-- OIDC discovery doc -----------------------|
  |                          |   (no code in URL, no existing token)        |
  |                          |-- authStore.logout()                         |
  |                          |   status=loaded, isLoggedIn=false            |
  |                          |   effect fires → initCodeFlow()              |
  |<-- redirect to Keycloak login page ------------------------------------->|
  |                          |                          |                    |
  |-- user submits credentials ----------------------------------------->  |
  |<-- redirect to http://localhost:4200?code=ABC&state=XYZ --------------- |
  |                          |                          |                    |
  |-- navigate to app ------>|                          |                    |
  |                          |-- loadDiscoveryDocumentAndTryLogin()          |
  |                          |   code=ABC found in URL                      |
  |                          |-- POST /token (code + PKCE verifier) ------->|
  |                          |<-- { access_token, id_token, refresh_token }-|
  |                          |   tokens stored; URL cleared                 |
  |                          |-- initAuth() → isAuthenticated()=true        |
  |                          |-- GET /auth/users/account -----Bearer------->|
  |                          |                          |-- validate JWT --->|
  |                          |                          |<-- JWKS -----------|
  |                          |                          |   extract email    |
  |                          |                          |   findByEmail(DB)  |
  |                          |<-- UserDTO --------------------------         |
  |                          |-- GET /auth/users/{id}/permissions ---------->|
  |                          |<-- ["permission.a", "permission.b"] ---------  |
  |                          |   ngxPermissions loaded                      |
  |                          |   isLoggedIn=true → navigate /home           |
```

---

## Token Refresh

`setupAutomaticSilentRefresh()` (called during `initializeAndTryLogin`) uses a hidden iframe to perform a silent token refresh via `prompt=none` before the access token expires. No user interaction is required. If the Keycloak session has expired the silent refresh fails and the user is redirected to the login page.

---

## Logout

```ts
authService.logout()
  → authStore.logout()          // clears store state
  → oauthConfigService.logout() // OAuthService.revokeTokenAndLogout()
                                 // POSTs to Keycloak token revocation endpoint
                                 // then redirects to postLogoutRedirectUri (http://localhost:4200)
```
