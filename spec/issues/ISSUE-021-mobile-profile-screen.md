# ISSUE-021 — Mobile: Profile Screen

**Epic:** Profile  
**Labels:** `android`, `ui`  
**Depends on:** ISSUE-015  
**Blocks:** —

---

## Summary

Implement the profile and settings hub shown in `docs/v1/SafetySpot_profil.md`: a user
identity card (avatar, name, level, XP progress, three stat columns), a navigable list of
sections (progress, badges, settings, help), and a logout action. Data comes from
`GET /api/v1/users/{id}` and `GET /api/v1/stats/student/{id}`.

---

## Acceptance Criteria

- [ ] Header shows title *"Profil"* and a settings gear icon (navigates to Settings stub)
- [ ] Identity card shows: avatar, full name, *"Level N"*, XP `ProgressBar` ("2.450 / 3.000 XP" format), and three stat columns: Punkte (formatted), Tage Streak, Abzeichen count
- [ ] Navigation list renders two labeled blocks: **Progress** (*Mein Fortschritt*, *Abzeichen*, *Meine Szenarien*) and **Support** (*Einstellungen*, *Hilfe & Feedback*), each row with a right-arrow
- [ ] **Abmelden** row at the bottom uses `BrandGreen`-tinted red text and an exit icon; tapping it calls `POST /api/v1/auth/logout`, clears `TokenManager`, and navigates back to `Auth` popping the full back stack
- [ ] Loading and error states handled with skeletons and `ErrorState`
- [ ] `ProfileViewModelTest` covers: data load, logout flow; `ProfileScreenTest` verifies identity card and logout row

---

## Technical Details

### Files to create

```
data/remote/api/UserApi.kt
data/remote/dto/UserDto.kt
data/repository/ProfileRepositoryImpl.kt
domain/repository/ProfileRepository.kt
domain/model/UserProfile.kt
ui/profile/ProfileScreen.kt
ui/profile/ProfileViewModel.kt
ui/profile/ProfileUiState.kt
```

### `UserApi`

```kotlin
interface UserApi {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Long): Response<UserDto>

    @GET("stats/student/{id}")
    suspend fun getStudentStats(@Path("id") id: Long): Response<StudentStatsDto>
}
```

### `ProfileUiState`

```kotlin
sealed class ProfileUiState {
    data object Loading : ProfileUiState()
    data class Content(
        val profile: UserProfile
    ) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
    data object LoggedOut : ProfileUiState()   // triggers nav to Auth
}

data class UserProfile(
    val id: Long,
    val displayName: String,
    val level: Int,
    val xp: Int,
    val xpForNextLevel: Int,
    val points: Int,
    val streakDays: Int,
    val badgeCount: Int,
    val avatarKey: String?
)
```

### `ProfileViewModel`

```kotlin
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    val uiState: StateFlow<ProfileUiState>

    init { loadProfile() }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()           // POST /auth/logout — fire and forget
            tokenManager.clearTokens()
            _uiState.value = ProfileUiState.LoggedOut
        }
    }
}
```

### Screen layout

```
Column {
    ProfileHeader(onSettingsClick = { navController.navigate(Settings) })

    // Identity card
    Card {
        AvatarImage(profile.avatarKey)
        Text(profile.displayName, style = headlineMedium)
        Text("Level ${profile.level}")
        ProgressBar(progress = profile.xp / profile.xpForNextLevel.toFloat(),
                    label = "${formatScore(profile.xp)} / ${formatScore(profile.xpForNextLevel)} XP")
        Row {
            StatColumn(formatScore(profile.points), "Punkte")
            StatColumn("${profile.streakDays}", "Tage Streak")
            StatColumn("${profile.badgeCount}", "Abzeichen")
        }
    }

    // Navigation lists
    ProfileSection("Fortschritt") {
        ProfileNavRow("Mein Fortschritt") { ... }
        ProfileNavRow("Abzeichen") { ... }
        ProfileNavRow("Meine Szenarien") { ... }
    }
    ProfileSection("Support") {
        ProfileNavRow("Einstellungen") { ... }
        ProfileNavRow("Hilfe & Feedback") { ... }
    }

    // Logout
    ProfileNavRow("Abmelden", textColor = Color.Red, icon = Icons.AutoMirrored.Outlined.Logout) {
        viewModel.logout()
    }
}
```

### Test class: `ProfileViewModelTest` (coroutines-test + Turbine)

| Test | Verifies |
|------|----------|
| `init_loadsProfile_emitsContent()` | state → Loading → Content |
| `logout_clearsTokens_emitsLoggedOut()` | `LoggedOut` state, tokens cleared |
| `logout_callsLogoutEndpoint()` | `AuthApi.logout` invoked |

### Test class: `ProfileScreenTest` (Compose UI)

| Test | Verifies |
|------|----------|
| `profileScreen_showsIdentityCard()` | name + level visible |
| `profileScreen_logoutRow_visible()` | Abmelden row present |

---

## Out of Scope

- Badges gallery screen (post-MVP)
- Settings screen content (stub navigation row only)
- Help & Feedback screen (stub)
- Avatar image upload
