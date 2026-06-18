# ISSUE-014 — Mobile: Networking & Auth Infrastructure

**Epic:** Auth  
**Labels:** `android`, `networking`, `auth`  
**Depends on:** ISSUE-012  
**Blocks:** ISSUE-015, ISSUE-016

---

## Summary

Wire the Retrofit client, Moshi converters, OkHttp interceptors, Hilt DI modules, and
`TokenManager`. This is pure infrastructure — no screens, but every screen that touches
the network depends on it. Can be developed in parallel with ISSUE-013.

---

## Acceptance Criteria

- [ ] `NetworkModule` provides a singleton `Retrofit` instance with `API_BASE_URL`, Moshi converter, `AuthInterceptor`, `TokenAuthenticator`, and a debug `HttpLoggingInterceptor`
- [ ] `AuthInterceptor` reads the access token from `TokenManager` synchronously and injects `Authorization: Bearer <token>` on every call; unauthenticated calls (no stored token) are forwarded unchanged
- [ ] `TokenAuthenticator` on `401`: calls `POST /api/v1/auth/refresh` inline, stores the new tokens, retries the original request once; on refresh failure it clears all stored tokens and returns `null` (triggers global logout signal)
- [ ] `TokenManager` wraps DataStore Preferences and exposes: `suspend fun saveTokens(...)`, `suspend fun clearTokens()`, `val authState: Flow<AuthState>` (enum: `Authenticated`, `Guest`, `LoggedOut`)
- [ ] `NetworkResult<T>` sealed class: `Success(data: T)`, `Error(exception: ApiException)`, `Loading`; `safeApiCall { }` extension maps HTTP error envelopes (`{status, error, message}`) to typed `ApiException` subclasses
- [ ] `DatabaseModule` provides a singleton `SafetySpotDatabase` (Room, in-memory for tests)
- [ ] `DataStoreModule` provides the token `DataStore<Preferences>`
- [ ] `RepositoryModule` binds repository interfaces to implementations (empty impls for now)
- [ ] Unit test: `AuthInterceptor` injects header when token is present; `TokenAuthenticator` clears tokens and returns null when refresh endpoint returns 401

---

## Technical Details

### Files to create

```
di/NetworkModule.kt
di/DatabaseModule.kt
di/DataStoreModule.kt
di/RepositoryModule.kt
data/remote/interceptor/AuthInterceptor.kt
data/remote/interceptor/TokenAuthenticator.kt
data/remote/NetworkResult.kt              (sealed + safeApiCall helper)
data/remote/ApiException.kt              (sealed: InvalidCredentials, NotFound, Forbidden,
                                          UsernameTaken, ScenarioLocked, Validation, Network)
data/local/SafetySpotDatabase.kt
domain/auth/AuthState.kt                  (sealed: Authenticated(userId, role), Guest, LoggedOut)
data/token/TokenManager.kt
```

### `TokenManager`

```kotlin
class TokenManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    val authState: Flow<AuthState>
        get() = dataStore.data.map { prefs ->
            when {
                prefs[GUEST_MODE] == true -> AuthState.Guest
                prefs[ACCESS_TOKEN] != null -> AuthState.Authenticated(
                    userId = prefs[USER_ID]!!,
                    role   = Role.valueOf(prefs[ROLE]!!)
                )
                else -> AuthState.LoggedOut
            }
        }

    suspend fun saveTokens(response: LoginResponse, guestMode: Boolean = false) { ... }
    suspend fun clearTokens() { ... }
    suspend fun getAccessToken(): String? { ... }
    suspend fun getRefreshToken(): String? { ... }
}
```

### `NetworkResult` + `safeApiCall`

```kotlin
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val exception: ApiException) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
}

suspend fun <T> safeApiCall(call: suspend () -> Response<T>): NetworkResult<T> {
    return try {
        val response = call()
        if (response.isSuccessful) {
            NetworkResult.Success(response.body()!!)
        } else {
            val envelope = parseErrorEnvelope(response.errorBody())
            NetworkResult.Error(ApiException.from(envelope))
        }
    } catch (e: IOException) {
        NetworkResult.Error(ApiException.Network(e.message))
    }
}
```

### `AuthInterceptor`

```kotlin
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { tokenManager.getAccessToken() }
        val request = if (token != null) {
            chain.request().newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else chain.request()
        return chain.proceed(request)
    }
}
```

### `TokenAuthenticator`

```kotlin
class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val authApi: AuthApi        // direct Retrofit instance without authenticator
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = runBlocking { tokenManager.getRefreshToken() } ?: return null
        val result = runBlocking { authApi.refresh(RefreshTokenRequest(refreshToken)) }
        return if (result.isSuccessful && result.body() != null) {
            runBlocking { tokenManager.saveTokens(result.body()!!) }
            response.request.newBuilder()
                .header("Authorization", "Bearer ${result.body()!!.accessToken}")
                .build()
        } else {
            runBlocking { tokenManager.clearTokens() }
            null    // propagates 401 upstream → NavHost observes AuthState.LoggedOut
        }
    }
}
```

### Test class: `AuthInterceptorTest` (MockWebServer)

| Test | Verifies |
|------|----------|
| `intercept_withToken_addsAuthorizationHeader()` | header present |
| `intercept_noToken_doesNotAddHeader()` | no spurious header |

### Test class: `TokenAuthenticatorTest` (MockWebServer + fake DataStore)

| Test | Verifies |
|------|----------|
| `authenticate_refreshSucceeds_retries()` | new token on retry request |
| `authenticate_refreshFails_clearsTokens()` | DataStore wiped, returns null |

---

## Out of Scope

- Auth screen UI (ISSUE-016)
- Room entities/DAOs (ISSUE-022)
- Actual Retrofit service interfaces (created per-feature in ISSUE-016–021)
