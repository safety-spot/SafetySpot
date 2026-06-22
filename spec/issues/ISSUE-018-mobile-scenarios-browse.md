# ISSUE-018 — Mobile: Scenarios Browse Screen

**Epic:** Content  
**Labels:** `android`, `ui`  
**Depends on:** ISSUE-015  
**Blocks:** ISSUE-019

---

## Summary

Implement the scenario catalogue screen shown in `docs/v1/SafetySpot_szenarienanzeigen.md`:
a search bar, filter tabs (Alle / Neu / Beliebt / Abgeschlossen), and a vertical list of
`ScenarioCard`s. Data comes from `GET /api/v1/scenarios`. Tapping a card navigates to
`ScenarioPlay`. Scenario summaries are cached in Room so the list is available
immediately on re-open.

---

## Acceptance Criteria

- [ ] Screen shows title *"Szenarien"*, a full-width `SearchBar` (with search + filter icons), and a `FilterTabRow` with tabs: Alle (active by default), Neu, Beliebt, Abgeschlossen
- [ ] Each `ScenarioCard` shows: category icon, title, subtitle, `DifficultyChip`, task count, and a *"Neu"* `TagChip` when `isNew = true`
- [ ] Searching filters the list client-side; filter tabs pass a server-side query param to the API
- [ ] Room cache is populated on successful fetch; the cached list is shown immediately while a background refresh is in-flight
- [ ] Tapping a `ScenarioCard` navigates to `ScenarioPlay(scenarioId)`
- [ ] Loading, error, and empty-state composables are shown appropriately
- [ ] `ScenariosViewModel` exposes `StateFlow<ScenariosUiState>`; search query is a separate `MutableStateFlow` debounced by 300 ms
- [ ] `ScenariosViewModelTest` covers: initial load, search filtering, filter-tab switching, cache hit; `ScenariosScreenTest` verifies the list and search bar render

---

## Technical Details

### Files to create

```
data/remote/api/ScenarioApi.kt
data/remote/dto/ScenarioDto.kt
data/local/dao/ScenarioDao.kt
data/local/entity/ScenarioEntity.kt
data/repository/ScenarioRepositoryImpl.kt
domain/repository/ScenarioRepository.kt
domain/model/ScenarioSummary.kt
ui/scenarios/ScenariosScreen.kt
ui/scenarios/ScenariosViewModel.kt
ui/scenarios/ScenariosUiState.kt
```

### `ScenarioApi`

```kotlin
interface ScenarioApi {
    @GET("scenarios")
    suspend fun getScenarios(
        @Query("status")    status: String = "PUBLISHED",
        @Query("filter")    filter: String? = null,   // "NEW" | "POPULAR" | "COMPLETED"
        @Query("search")    search: String? = null,
        @Query("categoryId") categoryId: Long? = null
    ): Response<List<ScenarioDto>>
}
```

### `ScenariosUiState`

```kotlin
sealed class ScenariosUiState {
    data object Loading : ScenariosUiState()
    data class Content(
        val scenarios: List<ScenarioSummary>,
        val activeFilter: ScenarioFilter,
        val searchQuery: String
    ) : ScenariosUiState()
    data class Error(val message: String) : ScenariosUiState()
}

enum class ScenarioFilter { ALL, NEW, POPULAR, COMPLETED }
```

### `ScenariosViewModel`

```kotlin
@HiltViewModel
class ScenariosViewModel @Inject constructor(
    private val scenarioRepository: ScenarioRepository
) : ViewModel() {
    val uiState: StateFlow<ScenariosUiState>
    val searchQuery = MutableStateFlow("")

    init {
        // Debounce search, merge with filter changes, collect from repository
        viewModelScope.launch {
            searchQuery.debounce(300).combine(activeFilter) { query, filter ->
                scenarioRepository.getScenarios(filter, query)
            }.collect { ... }
        }
    }

    fun onFilterSelected(filter: ScenarioFilter) { ... }
    fun onSearchQueryChanged(query: String) { searchQuery.value = query }
    fun retry() { ... }
}
```

### Repository SSOT pattern

```kotlin
// ScenarioRepositoryImpl
override fun getScenarios(filter: ScenarioFilter, search: String): Flow<NetworkResult<List<ScenarioSummary>>> = flow {
    emit(NetworkResult.Loading)
    // 1. Emit cached data immediately
    val cached = scenarioDao.getAll().first()
    if (cached.isNotEmpty()) emit(NetworkResult.Success(cached.map { it.toDomain() }))
    // 2. Refresh from network
    val result = safeApiCall { scenarioApi.getScenarios(...) }
    if (result is NetworkResult.Success) {
        scenarioDao.upsertAll(result.data.map { it.toEntity() })
        emit(NetworkResult.Success(result.data.map { it.toDomain() }))
    } else emit(result)
}
```

### Test class: `ScenariosViewModelTest` (coroutines-test + Turbine)

| Test | Verifies |
|------|----------|
| `init_loadsScenarios_emitsContent()` | state → Loading → Content |
| `searchQuery_debounced_triggersFilter()` | 300 ms debounce respected |
| `filterSelected_NEW_updatesState()` | `activeFilter = NEW` |
| `cacheHit_emitsBeforeNetworkResult()` | cache emitted first |

### Test class: `ScenariosScreenTest` (Compose UI)

| Test | Verifies |
|------|----------|
| `scenariosScreen_showsSearchBar()` | SearchBar visible |
| `scenariosScreen_tapCard_navigatesToPlay()` | navigation triggered |

---

## Out of Scope

- Pagination / infinite scroll (post-MVP)
- Category filter coming from home screen (pass `categoryId` query param; handled in ViewModel)
