# ISSUE-012 — Mobile: Project Foundation & Build Configuration

**Epic:** Foundation  
**Labels:** `android`, `foundation`  
**Depends on:** —  
**Blocks:** All other mobile issues

---

## Summary

Bootstrap the Android project properly: add the required dependency groups to the version
catalog, wire Hilt / KSP / Room plugin configuration, set up the `SafetySpotApp` application
class, configure `BuildConfig` fields for `API_BASE_URL` per build type, and move the API
contract DTOs into `sscommon/contract/` so both the app and future integrations share them.
This is the shared foundation all other mobile issues build on.

---

## Acceptance Criteria

- [ ] `gradle/libs.versions.toml` contains catalog entries for: Hilt + compiler, KSP plugin, Navigation-Compose, Retrofit + Moshi (+ codegen), OkHttp logging, Room (runtime + ktx + compiler), DataStore Preferences, Coil Compose, ViewModel-Compose, and test libs (coroutines-test, Turbine, MockWebServer)
- [ ] `app/build.gradle.kts` applies the `hilt`, `ksp`, and `room` plugins and wires all new dependencies through the catalog
- [ ] `SafetySpotApp.kt` (`@HiltAndroidApp`) is declared as `android:name` in `AndroidManifest.xml`
- [ ] `AndroidManifest.xml` declares `INTERNET` permission
- [ ] `BuildConfig.API_BASE_URL` = `"http://10.0.2.2:8080/api/v1/"` for `debug`; release placeholder pointing to Azure HTTPS
- [ ] `sscommon` `contract/` package replaces the placeholder `Main.kt` with pure-Kotlin data classes mirroring the backend DTO shapes in `spec/plan.md §7` — no Android, Moshi, or Room annotations
- [ ] `./gradlew :app:assembleDebug` succeeds
- [ ] Placeholder `ExampleUnitTest` / `ExampleInstrumentedTest` replaced by a smoke test verifying `BuildConfig.API_BASE_URL` is non-blank

---

## Technical Details

### Files to create / modify

```
ssmobile/gradle/libs.versions.toml              (modify — add new entries)
ssmobile/app/build.gradle.kts                   (modify — new plugins + deps)
ssmobile/app/src/main/AndroidManifest.xml        (modify)
ssmobile/app/src/main/java/.../SafetySpotApp.kt  (create)
sscommon/src/main/kotlin/spot/safety/contract/   (create package tree)
```

### `SafetySpotApp`

```kotlin
@HiltAndroidApp
class SafetySpotApp : Application()
```

### `sscommon` contract package layout

```
spot.safety.contract
├── auth/        LoginRequest, LoginResponse, RefreshTokenRequest
├── user/        UserResponse, CreateUserRequest, UpdateUserRequest
├── category/    CategoryResponse
├── scenario/    ScenarioResponse, ScenarioDetailResponse
├── task/        TaskResponse, TaskOptionResponse
├── attempt/     AttemptResponse, AnswerItem, SubmitAnswersRequest
├── progress/    ProgressResponse, ScenarioProgressResponse
└── leaderboard/ LeaderboardEntryResponse, LeaderboardResponse
```

Each class is a Kotlin `data class` with nullable defaults for optional fields. Fields match
the JSON payload shapes from `spec/plan.md §5` exactly.

### `BuildConfig` fields

```kotlin
// debug defaultConfig
buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8080/api/v1/\"")

// release buildType
buildConfigField("String", "API_BASE_URL", "\"https://api.safetyspot.example.com/api/v1/\"")
```

### Smoke test

```kotlin
class BuildConfigTest {
    @Test fun apiBaseUrl_isNotBlank() {
        assertTrue(BuildConfig.API_BASE_URL.startsWith("http"))
    }
}
```

---

## Out of Scope

- DI modules (ISSUE-014)
- Room schema or DAOs (ISSUE-022)
- Any UI or screen code
