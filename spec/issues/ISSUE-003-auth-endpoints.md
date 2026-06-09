# ISSUE-003 — Auth Endpoints (Login / Refresh / Logout)

**Epic:** Auth  
**Labels:** `backend`, `auth`  
**Depends on:** ISSUE-002, ISSUE-004 (User entity must exist)  
**Blocks:** —

---

## Summary

Expose the three auth endpoints the Android app uses for session management. Login exchanges
credentials for a JWT pair. Refresh issues a new access token. Logout is a client-side
no-op at the server for now (stateless JWT — no token blacklist in MVP).

---

## Acceptance Criteria

- [ ] `POST /api/v1/auth/login` with valid credentials returns `200` with `accessToken`, `refreshToken`, `expiresIn`, `userId`, `role`
- [ ] `POST /api/v1/auth/login` with wrong password returns `401`
- [ ] `POST /api/v1/auth/login` with inactive user (`active = false`) returns `403`
- [ ] `POST /api/v1/auth/login` updates `user.lastLoginAt` in the database
- [ ] `POST /api/v1/auth/refresh` with a valid refresh token returns a new `accessToken`
- [ ] `POST /api/v1/auth/refresh` with an expired/invalid token returns `401`
- [ ] `POST /api/v1/auth/logout` returns `204` (no body) for any authenticated caller
- [ ] `AuthControllerTest` covers all cases above using `@WebMvcTest` + MockMvc

---

## Technical Details

### Files to create

```
controller/AuthController.java
service/AuthService.java              (interface)
service/impl/AuthServiceImpl.java
dto/auth/LoginRequest.java
dto/auth/LoginResponse.java
dto/auth/RefreshTokenRequest.java
```

### Endpoints

#### `POST /api/v1/auth/login`

Request:
```json
{ "username": "string", "password": "string" }
```

Response `200`:
```json
{
  "accessToken":  "eyJ...",
  "refreshToken": "eyJ...",
  "expiresIn":    3600,
  "userId":       42,
  "role":         "STUDENT"
}
```

Errors:
- `401 INVALID_CREDENTIALS` — user not found or wrong password
- `403 ACCOUNT_INACTIVE` — `user.active == false`

#### `POST /api/v1/auth/refresh`

Request:
```json
{ "refreshToken": "eyJ..." }
```

Response `200`: same shape as login response  
Error: `401 INVALID_CREDENTIALS`

#### `POST /api/v1/auth/logout`

No body.  
Response `204` — endpoint exists for future token blacklisting; for MVP it just returns 204.

### `AuthService` interface

```java
public interface AuthService {
    LoginResponse login(LoginRequest request);
    LoginResponse refresh(RefreshTokenRequest request);
    void logout(String bearerToken);
}
```

### `AuthServiceImpl` logic

**login:**
1. Load `User` by `username` via `UserRepository` — throw `EntityNotFoundException` → handled as `401` (remap in `GlobalExceptionHandler`)
2. Verify password with `BCryptPasswordEncoder.matches(raw, hash)` — throw `BadCredentialsException` on mismatch
3. Check `user.active` — throw if false
4. Build `UserPrincipal`, generate token pair via `JwtTokenProvider`
5. Update `user.lastLoginAt = Instant.now()`, save
6. Return `LoginResponse`

**refresh:**
1. Validate refresh token via `JwtTokenProvider`
2. Extract `userId`, load user, build `UserPrincipal`
3. Generate new access token (and optionally new refresh token)
4. Return `LoginResponse`

### DTOs

```java
// LoginRequest
record LoginRequest(
    @NotBlank String username,
    @NotBlank String password
) {}

// LoginResponse
record LoginResponse(
    String accessToken,
    String refreshToken,
    long expiresIn,
    Long userId,
    Role role
) {}

// RefreshTokenRequest
record RefreshTokenRequest(
    @NotBlank String refreshToken
) {}
```

### Test class: `AuthControllerTest` (`@WebMvcTest(AuthController.class)`)

| Test method | Expected |
|-------------|----------|
| `login_validCredentials_returns200WithTokens()` | 200, accessToken present |
| `login_wrongPassword_returns401()` | 401 |
| `login_inactiveUser_returns403()` | 403 |
| `login_missingFields_returns400()` | 400 validation error |
| `refresh_validToken_returns200()` | 200, new accessToken |
| `refresh_invalidToken_returns401()` | 401 |
| `logout_authenticated_returns204()` | 204 |

---

## Out of Scope

- Token blacklisting on logout
- Rate limiting on login (post-MVP)
