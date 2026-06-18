# ISSUE-017 — Mobile: Home Dashboard Screen

**Epic:** Dashboard  
**Labels:** `android`, `ui`  
**Depends on:** ISSUE-015  
**Blocks:** —

---

## Summary

Implement the main dashboard shown in `docs/v1/SafetySpot_home.md`: personalised greeting,
level and points metric cards, streak row, a "resume last scenario" banner, and a
categories grid with an "Alle anzeigen" shortcut. Data is pulled from
`GET /api/v1/progress` and `GET /api/v1/categories`.

---

## Acceptance Criteria

- [ ] Header shows *"Hallo, {firstName}!"* and a notification bell icon with a red dot when unread notifications exist (dot always shown for now — no backend endpoint yet)
- [ ] Two `MetricCard`s side-by-side: level badge (number + rank title e.g. "Sicherheitsprofi") and points (star + formatted score)
- [ ] `StreakRow` shows fire icon and *"✓ N Tage in Folge"*
- [ ] **Weitermachen** banner appears only when progress data contains an in-progress scenario; shows category graphic, title, subtopic, `ProgressBar` at the correct percentage, and a **Fortsetzen** button that navigates to `ScenarioPlay(scenarioId)`
- [ ] **Kategorien** grid renders one `CategoryTile` per category returned by the API; "Alle anzeigen" link navigates to the Scenarios tab
- [ ] Loading state shows skeleton/shimmer placeholders; error state shows `ErrorState` composable with a retry button
- [ ] `HomeViewModel` exposes `StateFlow<HomeUiState>`; fetches data on init
- [ ] `HomeViewModelTest` covers: success, loading, error, missing-resume-scenario cases
- [ ] `HomeScreenTest` (Compose UI) verifies greeting text, metric cards, and category tiles render

---

## Technical Details

### Files to create

```
data/remote/api/CategoryApi.kt
data/remote/api/ProgressApi.kt
data/remote/dto/CategoryDto.kt
data/remote/dto/ProgressDto.kt         (resume pointer + per-scenario progress)
data/repository/CategoryRepositoryImpl.kt
data/repository/ProgressRepositoryImpl.kt
domain/repository/CategoryRepository.kt
domain/repository/ProgressRepository.kt
domain/model/Category.kt
domain/model/ProgressSummary.kt        (continueScenario, userLevel, points, streak, categories)
ui/home/HomeScreen.kt
ui/home/HomeViewModel.kt
ui/home/HomeUiState.kt
```

### `HomeUiState`

```kotlin
sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Content(
        val displayName: String,
        val level: Int,
        val levelTitle: String,
        val points: Int,
        val streakDays: Int,
        val continueScenario: ContinueScenarioData?,   // null = no in-progress scenario
        val categories: List<Category>
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

data class ContinueScenarioData(
    val scenarioId: Long,
    val title: String,
    val subtitle: String,
    val categoryIconKey: String,
    val progressPercent: Float    // 0f–1f
)
```

> **Contract note:** `levelTitle`, `continueScenario`, and `categoryIconKey` are not yet in
> the backend API — see §F5 gap in `spec/plan.md`. Derive `levelTitle` client-side from
> `level` ranges for now; leave `continueScenario` null until the backend exposes it.

### `HomeViewModel`

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    val uiState: StateFlow<HomeUiState>

    init { loadDashboard() }
    fun retry() { loadDashboard() }
}
```

### `HomeScreen` layout outline

```
Column {
    // Header
    Row { Text("Hallo, $displayName!"); NotificationBell() }

    // Metrics
    Row { MetricCard(level badge); MetricCard(points) }
    StreakRow(streakDays)

    // Resume banner
    continueScenario?.let { ContinueBanner(it, onContinue = { navController.navigate(...) }) }

    // Categories
    SectionHeader("Kategorien", actionLabel = "Alle anzeigen", onAction = { navController.navigate(Scenarios) })
    LazyVerticalGrid { items(categories) { CategoryTile(it) } }
}
```

### Test class: `HomeViewModelTest` (coroutines-test + Turbine)

| Test | Verifies |
|------|----------|
| `init_loadsData_emitsContent()` | state → Loading → Content |
| `init_apiError_emitsError()` | Error state with message |
| `content_noContinueScenario_resumeBannerAbsent()` | `continueScenario == null` |
| `retry_afterError_reloadsData()` | second emission is Content |

### Test class: `HomeScreenTest` (Compose UI)

| Test | Verifies |
|------|----------|
| `homeScreen_showsGreeting()` | greeting text visible |
| `homeScreen_showsMetricCards()` | level + points cards present |
| `homeScreen_errorState_showsRetryButton()` | error composable rendered |

---

## Out of Scope

- Notification inbox screen
- Pulling real `levelTitle` from server (post-MVP; derive client-side for now)
- Pagination for categories
