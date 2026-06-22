package spot.safety.ssmobile.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import spot.safety.ssmobile.ui.auth.AuthScreen
import spot.safety.ssmobile.ui.components.SafetySpotBottomBar
import spot.safety.ssmobile.ui.home.HomeScreen
import spot.safety.ssmobile.ui.navigation.Destinations
import spot.safety.ssmobile.ui.navigation.TopLevelDestination
import spot.safety.ssmobile.ui.profile.ProfileDetailScreen
import spot.safety.ssmobile.ui.profile.ProfileUi
import spot.safety.ssmobile.ui.profile.ProfileScreen
import spot.safety.ssmobile.ui.ranking.LeaderboardEntryUi
import spot.safety.ssmobile.ui.ranking.RankingScreen
import spot.safety.ssmobile.ui.ranking.sampleLeaderboard
import spot.safety.ssmobile.ui.scenario.ScenarioPlayScreen
import spot.safety.ssmobile.ui.scenarios.ScenariosScreen
import spot.safety.ssmobile.ui.scenarios.sampleScenarios
import spot.safety.ssmobile.ui.theme.AppBackground
import spot.safety.ssmobile.ui.theme.SsmobileTheme

@Composable
fun SafetySpotApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val completedScenarioIds = remember { mutableStateListOf<Int>() }
    var sessionPoints by remember { mutableIntStateOf(2450) }
    val completedScenarios by remember {
        derivedStateOf { sampleScenarios.filter { it.id in completedScenarioIds } }
    }
    val sessionLevel = 12 + (sessionPoints - 2450).coerceAtLeast(0) / 120
    val sessionBadges = 24 + completedScenarioIds.size
    val leaderboardEntries = remember(sessionPoints) {
        sampleLeaderboard
            .filterNot { it.isCurrentUser }
            .plus(LeaderboardEntryUi(0, "Du (Max)", sessionPoints, "M", isCurrentUser = true))
            .sortedByDescending { it.score }
            .mapIndexed { index, entry -> entry.copy(rank = index + 1) }
    }
    val profile = ProfileUi(
        fullName = "Max Mustermann",
        level = sessionLevel,
        currentXp = sessionPoints,
        nextLevelXp = 3000,
        points = sessionPoints,
        streakDays = 7,
        badges = sessionBadges
    )
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val activeDestination = TopLevelDestination.entries.firstOrNull { it.route == currentRoute }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppBackground,
        bottomBar = {
            if (activeDestination != null) {
                SafetySpotBottomBar(
                    activeDestination = activeDestination,
                    onDestinationSelected = { destination ->
                        navController.navigateToTopLevel(destination)
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destinations.AUTH,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destinations.AUTH) {
                AuthScreen(
                    onAuthenticated = {
                        navController.navigate(Destinations.HOME) {
                            popUpTo(Destinations.AUTH) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Destinations.HOME) {
                HomeScreen(
                    level = sessionLevel,
                    points = sessionPoints,
                    completedScenarioCount = completedScenarioIds.size,
                    onShowAllCategories = { navController.navigateToTopLevel(TopLevelDestination.SCENARIOS) },
                    onContinueScenario = { navController.navigate(Destinations.scenarioPlayRoute(1)) },
                    onCategoryClick = { navController.navigateToTopLevel(TopLevelDestination.SCENARIOS) }
                )
            }
            composable(Destinations.SCENARIOS) {
                ScenariosScreen(
                    completedScenarioIds = completedScenarioIds.toSet(),
                    onScenarioClick = { scenario ->
                        navController.navigate(Destinations.scenarioPlayRoute(scenario.id))
                    }
                )
            }
            composable(Destinations.RANKING) {
                RankingScreen(entries = leaderboardEntries)
            }
            composable(Destinations.PROFILE) {
                ProfileScreen(
                    profile = profile,
                    onProgressClick = { navController.navigate(Destinations.PROFILE_PROGRESS) },
                    onBadgesClick = { navController.navigate(Destinations.PROFILE_BADGES) },
                    onMyScenariosClick = { navController.navigate(Destinations.PROFILE_SCENARIOS) },
                    onSettingsClick = { navController.navigate(Destinations.SETTINGS) },
                    onHelpClick = { navController.navigate(Destinations.HELP) },
                    onLogoutClick = {
                        navController.navigate(Destinations.AUTH) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Destinations.PROFILE_PROGRESS) {
                ProfileDetailScreen(
                    title = "Mein Fortschritt",
                    subtitle = "Hier landen spaeter deine abgeschlossenen Szenarien, XP und Lernziele.",
                    items = if (completedScenarios.isEmpty()) {
                        listOf("Noch kein Szenario abgeschlossen")
                    } else {
                        completedScenarios.map { "${it.title}: abgeschlossen" } +
                            "Punkte: $sessionPoints"
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Destinations.PROFILE_BADGES) {
                ProfileDetailScreen(
                    title = "Abzeichen",
                    subtitle = "Sammlung deiner Erfolge und Sicherheits-Meilensteine.",
                    items = buildList {
                        add("7 Tage Streak")
                        add("Erste Aufgabe geschafft")
                        if (completedScenarioIds.isNotEmpty()) {
                            add("${completedScenarioIds.size} Szenario abgeschlossen")
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Destinations.PROFILE_SCENARIOS) {
                ProfileDetailScreen(
                    title = "Meine Szenarien",
                    subtitle = "Szenarien, die du begonnen oder abgeschlossen hast.",
                    items = if (completedScenarios.isEmpty()) {
                        listOf("Noch keine abgeschlossenen Szenarien")
                    } else {
                        completedScenarios.map { it.title }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Destinations.SETTINGS) {
                ProfileDetailScreen(
                    title = "Einstellungen",
                    subtitle = "Platzhalter fuer App-Einstellungen und Kontooptionen.",
                    items = listOf("Benachrichtigungen", "Darstellung", "Datenschutz"),
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Destinations.HELP) {
                ProfileDetailScreen(
                    title = "Hilfe & Feedback",
                    subtitle = "Hier koennen spaeter Fragen, Feedback und Support landen.",
                    items = listOf("FAQ", "Feedback senden", "Problem melden"),
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(
                route = Destinations.SCENARIO_PLAY,
                arguments = listOf(navArgument(Destinations.SCENARIO_ID_ARG) { type = NavType.IntType })
            ) { backStackEntry ->
                val scenarioId = backStackEntry.arguments?.getInt(Destinations.SCENARIO_ID_ARG) ?: 1
                ScenarioPlayScreen(
                    scenarioId = scenarioId,
                    onScenarioCompleted = { earnedPoints ->
                        if (scenarioId !in completedScenarioIds) {
                            sessionPoints += earnedPoints
                            completedScenarioIds.add(scenarioId)
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

private fun androidx.navigation.NavHostController.navigateToTopLevel(destination: TopLevelDestination) {
    navigate(destination.route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Preview(showBackground = true)
@Composable
private fun SafetySpotAppPreview() {
    SsmobileTheme {
        SafetySpotApp()
    }
}
