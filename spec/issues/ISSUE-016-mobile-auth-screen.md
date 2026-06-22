# ISSUE-016 — Mobile: Auth Screen (Login / Register / Guest)

**Epic:** Auth  
**Labels:** `android`, `auth`, `ui`  
**Depends on:** ISSUE-014, ISSUE-015  
**Blocks:** —

---

## Summary

Implement the onboarding and authentication screen shown in `docs/v1/SafetySpot_Anmelden.md`:
gradient background, centred logo, tagline, and three actions — **Anmelden** (login),
**Registrieren** (register), and **Als Gast ausprobieren** (guest). Login and register
each expand an inline form, submit to the backend, persist tokens, and navigate to Home.
Guest mode sets the `Guest` auth state without a token and also navigates to Home.

---

## Acceptance Criteria

- [ ] Screen shows: gradient background, `SafetySpotIcon`, "SafetySpot" title (dark-blue / green split), slogan *"Lern. Sicher. Stark."*, three action elements
- [ ] **Anmelden** button expands/navigates to a login form; `POST /api/v1/auth/login`; on success tokens are saved and user is navigated to Home
- [ ] **Registrieren** button expands/navigates to a register form; `POST /api/v1/users` (self-registration, role = STUDENT); on success logs in and navigates to Home
- [ ] **Als Gast ausprobieren** sets `AuthState.Guest` in `TokenManager` and navigates to Home
- [ ] `InvalidCredentials` error surfaces as an inline error message below the form (not a dialog)
- [ ] Login and register buttons show a loading indicator while the network call is in-flight
- [ ] `AuthViewModel` exposes `StateFlow<AuthUiState>` consumed by the screen
- [ ] `AuthViewModelTest` covers all state transitions; `AuthScreenTest` (Compose UI) verifies the three action elements are visible

---

## Technical Details

### Files to create

```
data/remote/api/AuthApi.kt
data/repository/AuthRepositoryImpl.kt
domain/repository/AuthRepository.kt
ui/auth/AuthScreen.kt
ui/auth/AuthViewModel.kt
ui/auth/AuthUiState.kt
```

### `AuthApi`

```kotlin
interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshTokenRequest): Response<LoginResponse>

    @POST("auth/logout")
    suspend fun logout(): Response<Unit>
}
```

### `AuthRepository`

```kotlin
interface AuthRepository {
    suspend fun login(username: String, password: String): NetworkResult<LoginResponse>
    suspend fun register(username: String, password: String): NetworkResult<UserResponse>
    suspend fun logout()
    suspend fun setGuestMode()
}
```

### `AuthUiState`

```kotlin
sealed class AuthUiState {
    data object Idle : AuthUiState()
    data object Loading : AuthUiState()
    data class Error(val message: String) : AuthUiState()
    data object Success : AuthUiState()    // triggers navigation in the composable
}
```

### `AuthViewModel`

```kotlin
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    val tokenManager: TokenManager       // accessed by MainActivity for start destination
) : ViewModel() {
    val uiState: StateFlow<AuthUiState>

    fun login(username: String, password: String) { ... }
    fun register(username: String, password: String) { ... }
    fun continueAsGuest() { ... }
}
```

### `AuthScreen` layout structure

```
GradientBackground {
    Column(horizontalAlignment = CenterHorizontally) {
        SafetySpotIcon (Image, ~120dp)
        "SafetySpot" title (BrandBlue + BrandGreen)
        slogan Text
        Spacer
        PillButton("Anmelden", variant = Solid, onClick = { showLoginForm = true })
        PillButton("Registrieren", variant = Outlined, ...)
        TextButton("Als Gast ausprobieren", ...)
        // Login / Register form shown as AnimatedVisibility or bottom sheet
    }
}
```

### Test class: `AuthViewModelTest` (coroutines-test + Turbine)

| Test | Verifies |
|------|----------|
| `login_validCredentials_emitsSuccess()` | state → Loading → Success |
| `login_invalidCredentials_emitsError()` | state → Loading → Error |
| `login_networkError_emitsError()` | IOException mapped to Error |
| `continueAsGuest_setsGuestState()` | `TokenManager.authState` = Guest |

### Test class: `AuthScreenTest` (Compose UI)

| Test | Verifies |
|------|----------|
| `authScreen_showsThreeActions()` | Anmelden + Registrieren + Gast visible |
| `authScreen_loginError_showsInlineMessage()` | error text rendered |
| `authScreen_loading_showsIndicator()` | progress shown |

---

## Out of Scope

- Password recovery (no email in the system)
- Remember-me / biometric login
- School/class selection during self-registration (teachers enrol students manually per ISSUE-006 backend)
