# ISSUE-020 — Mobile: Ranking Screen

**Epic:** Gamification  
**Labels:** `android`, `ui`, `gamification`  
**Depends on:** ISSUE-015  
**Blocks:** —

---

## Summary

Implement the leaderboard screen shown in `docs/v1/SafetySpot_schulranking.md`: three scope
tabs (Klasse / Schule / Weltweit), a podium for the top-3 players, and a scrollable list for
ranks 4 onwards. The current user's row is highlighted in light green. Data comes from
`GET /api/v1/leaderboard/class/{id}`, `/leaderboard/school/{id}`, and `/leaderboard`.

---

## Acceptance Criteria

- [ ] Three scope-toggle pills at the top: **Klasse** (default), Schule, Weltweit; active pill is solid green
- [ ] Top-3 players are rendered as a `PodiumColumn` trio (centre = 1st / gold, left = 2nd / silver, right = 3rd / bronze) with avatar, name, and formatted score
- [ ] Ranks 4+ rendered as `LeaderboardRow`s in a `LazyColumn`; the row where `isCurrentUser = true` has a light-green background
- [ ] Switching scope tabs fetches from the corresponding endpoint; a loading indicator covers the list during fetch
- [ ] Error and empty states handled
- [ ] Guest users see a prompt to register instead of leaderboard data
- [ ] `RankingViewModelTest` covers: scope switching, current-user highlighting, empty list, error; `RankingScreenTest` verifies podium and highlighted row

---

## Technical Details

### Files to create

```
data/remote/api/LeaderboardApi.kt
data/remote/dto/LeaderboardDto.kt
data/repository/LeaderboardRepositoryImpl.kt
domain/repository/LeaderboardRepository.kt
domain/model/LeaderboardEntry.kt
ui/ranking/RankingScreen.kt
ui/ranking/RankingViewModel.kt
ui/ranking/RankingUiState.kt
```

### `LeaderboardApi`

```kotlin
interface LeaderboardApi {
    @GET("leaderboard")
    suspend fun getGlobal(): Response<List<LeaderboardEntryDto>>

    @GET("leaderboard/school/{schoolId}")
    suspend fun getSchool(@Path("schoolId") schoolId: Long): Response<List<LeaderboardEntryDto>>

    @GET("leaderboard/class/{classId}")
    suspend fun getClass(@Path("classId") classId: Long): Response<List<LeaderboardEntryDto>>
}
```

### `RankingUiState`

```kotlin
sealed class RankingUiState {
    data object Loading : RankingUiState()
    data class Content(
        val scope: LeaderboardScope,
        val entries: List<LeaderboardEntry>    // sorted by rank; isCurrentUser flags the row
    ) : RankingUiState()
    data class Error(val message: String) : RankingUiState()
    data object GuestPrompt : RankingUiState()
}

enum class LeaderboardScope { CLASS, SCHOOL, WORLD }
```

### `RankingViewModel`

```kotlin
@HiltViewModel
class RankingViewModel @Inject constructor(
    private val leaderboardRepository: LeaderboardRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    val uiState: StateFlow<RankingUiState>

    init { loadLeaderboard(LeaderboardScope.CLASS) }

    fun onScopeSelected(scope: LeaderboardScope) { loadLeaderboard(scope) }
}
```

### Screen layout

```
Column {
    ScopeTabRow(activeScope, onScopeSelected)

    when (uiState) {
        Loading -> LoadingIndicator()
        GuestPrompt -> GuestRegisterPrompt()
        Content -> {
            // Podium for top 3
            Row(horizontalArrangement = SpaceEvenly) {
                PodiumColumn(rank=2, entries[1])
                PodiumColumn(rank=1, entries[0])   // centre
                PodiumColumn(rank=3, entries[2])
            }
            // Remaining ranks
            LazyColumn {
                items(entries.drop(3)) { LeaderboardRow(it) }
            }
        }
        Error -> ErrorState(onRetry = { onScopeSelected(scope) })
    }
}
```

### Test class: `RankingViewModelTest` (coroutines-test + Turbine)

| Test | Verifies |
|------|----------|
| `init_loadsClassScope_emitsContent()` | default scope = CLASS |
| `onScopeSelected_SCHOOL_fetchesSchoolEndpoint()` | correct API called |
| `content_currentUserRow_isMarked()` | at least one `isCurrentUser = true` |
| `guestMode_emitsGuestPrompt()` | `GuestPrompt` state |

### Test class: `RankingScreenTest` (Compose UI)

| Test | Verifies |
|------|----------|
| `rankingScreen_showsThreeScopeTabs()` | Klasse + Schule + Weltweit |
| `rankingScreen_currentUserRow_highlighted()` | green background present |

---

## Out of Scope

- Animated podium entrance
- Historical rank movement indicators (post-MVP)
