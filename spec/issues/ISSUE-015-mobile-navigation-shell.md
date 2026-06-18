# ISSUE-015 — Mobile: Navigation Shell

**Epic:** Foundation  
**Labels:** `android`, `navigation`  
**Depends on:** ISSUE-013, ISSUE-014  
**Blocks:** ISSUE-016, ISSUE-017, ISSUE-018, ISSUE-019, ISSUE-020, ISSUE-021

---

## Summary

Set up the single-Activity Compose navigation graph, typed route destinations, the bottom
tab bar (shown only on the four top-level tabs), and the logic that picks the start
destination based on `TokenManager.authState`. Individual screens are implemented in later
issues — this issue wires the skeleton and ensures navigation compiles end-to-end with
placeholder composables.

---

## Acceptance Criteria

- [ ] `SafetySpotNavHost` defines routes for: `Auth`, `Home`, `Scenarios`, `ScenarioPlay(scenarioId: Long)`, `Ranking`, `Profile`
- [ ] `SafetySpotBottomBar` (from ISSUE-013) is shown only when the current destination is one of the four tab destinations; hidden on `Auth` and `ScenarioPlay`
- [ ] Start destination is derived from `TokenManager.authState` collected in `MainActivity`: `Authenticated` / `Guest` → `Home`; `LoggedOut` → `Auth`
- [ ] Global logout (emitted by `TokenAuthenticator` via `AuthState.LoggedOut`) pops the entire back stack and navigates to `Auth`
- [ ] Navigating back from `Auth` (e.g. system back) does not re-open the app — `Auth` is always the bottom of the stack for unauthenticated users
- [ ] `./gradlew :app:assembleDebug` passes with stub composables in place of real screens
- [ ] `NavigationTest` (Compose UI test) verifies: start destination is `Auth` when logged out; bottom bar is visible on Home and hidden on `Auth`

---

## Technical Details

### Files to create

```
ui/navigation/SafetySpotNavHost.kt
ui/navigation/Destinations.kt
ui/navigation/TopLevelDestination.kt    (enum used by BottomBar)
MainActivity.kt                         (replace scaffold stub)
```

### `Destinations`

```kotlin
sealed class Destinations(val route: String) {
    data object Auth      : Destinations("auth")
    data object Home      : Destinations("home")
    data object Scenarios : Destinations("scenarios")
    data class  ScenarioPlay(val scenarioId: Long) : Destinations("scenario/{scenarioId}") {
        companion object { const val ROUTE = "scenario/{scenarioId}" }
        fun buildRoute() = "scenario/$scenarioId"
    }
    data object Ranking   : Destinations("ranking")
    data object Profile   : Destinations("profile")
}
```

### `TopLevelDestination` (drives bottom bar)

```kotlin
enum class TopLevelDestination(
    val route: String,
    val labelRes: Int,
    val icon: ImageVector
) {
    HOME     ("home",      R.string.nav_home,      Icons.Outlined.Home),
    SCENARIOS("scenarios", R.string.nav_scenarios, Icons.Outlined.Description),
    RANKING  ("ranking",   R.string.nav_ranking,   Icons.Outlined.BarChart),
    PROFILE  ("profile",   R.string.nav_profile,   Icons.Outlined.Person)
}
```

### `SafetySpotNavHost` skeleton

```kotlin
@Composable
fun SafetySpotNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(navController, startDestination = startDestination, modifier = modifier) {
        composable(Destinations.Auth.route)      { /* AuthScreen — ISSUE-016 */ }
        composable(Destinations.Home.route)      { /* HomeScreen — ISSUE-017 */ }
        composable(Destinations.Scenarios.route) { /* ScenariosScreen — ISSUE-018 */ }
        composable(
            route = Destinations.ScenarioPlay.ROUTE,
            arguments = listOf(navArgument("scenarioId") { type = NavType.LongType })
        ) { /* ScenarioPlayScreen — ISSUE-019 */ }
        composable(Destinations.Ranking.route)   { /* RankingScreen — ISSUE-020 */ }
        composable(Destinations.Profile.route)   { /* ProfileScreen — ISSUE-021 */ }
    }
}
```

### `MainActivity` start-destination logic

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SsmobileTheme {
                val tokenManager: TokenManager = hiltViewModel<AuthViewModel>().tokenManager
                val authState by tokenManager.authState.collectAsStateWithLifecycle()

                val startDestination = when (authState) {
                    is AuthState.Authenticated, AuthState.Guest -> Destinations.Home.route
                    AuthState.LoggedOut -> Destinations.Auth.route
                }

                val navController = rememberNavController()
                val currentRoute = navController.currentBackStackEntryAsState()
                    .value?.destination?.route

                val showBottomBar = TopLevelDestination.entries
                    .any { it.route == currentRoute }

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) SafetySpotBottomBar(
                            activeDestination = ...,
                            onDestinationSelected = { navController.navigate(it.route) }
                        )
                    }
                ) { padding ->
                    SafetySpotNavHost(navController, startDestination, Modifier.padding(padding))
                }
            }
        }
    }
}
```

### Test class: `NavigationTest` (`@HiltAndroidTest` + `createAndroidComposeRule`)

| Test | Verifies |
|------|----------|
| `startDestination_loggedOut_isAuthScreen()` | Auth shown for `LoggedOut` state |
| `startDestination_authenticated_isHomeScreen()` | Home shown for `Authenticated` state |
| `bottomBar_hiddenOnAuthScreen()` | no bottom bar on Auth |
| `bottomBar_visibleOnHomeScreen()` | bottom bar present on Home |

---

## Out of Scope

- Actual screen content (ISSUE-016–021)
- Deep links from notifications
- Back-press handling beyond `popBackStack`
