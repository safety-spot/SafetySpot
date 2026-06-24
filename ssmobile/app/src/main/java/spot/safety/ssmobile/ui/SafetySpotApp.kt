package spot.safety.ssmobile.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import spot.safety.ssmobile.SsmobileApplication
import spot.safety.ssmobile.ui.auth.AuthScreen
import spot.safety.ssmobile.ui.auth.AuthViewModel
import spot.safety.ssmobile.ui.components.SafetySpotBottomBar
import spot.safety.ssmobile.ui.home.HomeScreen
import spot.safety.ssmobile.ui.home.HomeViewModel
import spot.safety.ssmobile.ui.navigation.Destinations
import spot.safety.ssmobile.ui.navigation.TopLevelDestination
import spot.safety.ssmobile.ui.profile.BadgeCollectionScreen
import spot.safety.ssmobile.ui.profile.HelpFeedbackScreen
import spot.safety.ssmobile.ui.profile.MyScenariosScreen
import spot.safety.ssmobile.ui.profile.ProfileProgressScreen
import spot.safety.ssmobile.ui.profile.ProfileScreen
import spot.safety.ssmobile.ui.profile.ProfileViewModel
import spot.safety.ssmobile.ui.profile.SettingsScreen
import spot.safety.ssmobile.ui.profile.sampleProfile
import spot.safety.ssmobile.ui.ranking.RankingScreen
import spot.safety.ssmobile.ui.ranking.RankingViewModel
import spot.safety.ssmobile.ui.scenario.ScenarioDetailScreen
import spot.safety.ssmobile.ui.scenario.ScenarioPlayScreen
import spot.safety.ssmobile.ui.scenario.ScenarioPlayViewModel
import spot.safety.ssmobile.ui.scenarios.ScenariosScreen
import spot.safety.ssmobile.ui.scenarios.ScenariosViewModel
import spot.safety.ssmobile.ui.scenarios.sampleScenarios
import spot.safety.ssmobile.ui.theme.AppBackground
import spot.safety.ssmobile.ui.theme.BrandBlue
import spot.safety.ssmobile.ui.theme.SsmobileTheme

@Composable
fun SafetySpotApp(modifier: Modifier = Modifier) {
    val app = LocalContext.current.applicationContext as SsmobileApplication
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()

    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.factory(app.authRepository))
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.factory(app.imageRepository, app.progressRepository, app.tokenStore))
    val scenariosViewModel: ScenariosViewModel = viewModel(factory = ScenariosViewModel.factory(app.imageRepository))
    val rankingViewModel: RankingViewModel = viewModel(factory = RankingViewModel.factory(app.leaderboardRepository, app.tokenStore))
    val profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.factory(app.progressRepository, app.tokenStore))

    // Session state for local completion tracking (used for profile/badges screens)
    val completedScenarioCategories = remember { mutableStateListOf<String>() }
    var sessionPoints by remember { mutableIntStateOf(0) }
    val completedScenarios by remember {
        derivedStateOf {
            sampleScenarios.filter { it.category in completedScenarioCategories }
        }
    }
    val startDestination = if (app.authRepository.isLoggedIn) Destinations.HOME else Destinations.AUTH

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
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destinations.AUTH) {
                AuthScreen(
                    authViewModel = authViewModel,
                    onAuthenticated = {
                        homeViewModel.load()
                        scenariosViewModel.load()
                        rankingViewModel.load()
                        profileViewModel.load()
                        navController.navigate(Destinations.HOME) {
                            popUpTo(Destinations.AUTH) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Destinations.HOME) {
                val homeState by homeViewModel.uiState.collectAsState()
                HomeScreen(
                    homeViewModel = homeViewModel,
                    onShowAllCategories = { navController.navigateToTopLevel(TopLevelDestination.SCENARIOS) },
                    onContinueScenario = {
                        homeState.categories.firstOrNull()?.let { category ->
                            navController.navigate(Destinations.scenarioDetailRoute(category))
                        }
                    },
                    onCategoryClick = { category ->
                        navController.navigate(Destinations.scenarioDetailRoute(category.name))
                    }
                )
            }
            composable(Destinations.SCENARIOS) {
                val uiState by scenariosViewModel.uiState.collectAsState()
                ScenariosScreen(
                    scenarios = uiState.scenarios,
                    completedScenarioIds = completedScenarios.map { it.id }.toSet(),
                    onScenarioClick = { scenario ->
                        val cat = scenario.category.ifEmpty { scenario.title }
                        navController.navigate(Destinations.scenarioDetailRoute(cat))
                    }
                )
            }
            composable(Destinations.RANKING) {
                RankingScreen(rankingViewModel = rankingViewModel)
            }
            composable(Destinations.PROFILE) {
                ProfileScreen(
                    profileViewModel = profileViewModel,
                    onProgressClick = { navController.navigate(Destinations.PROFILE_PROGRESS) },
                    onBadgesClick = { navController.navigate(Destinations.PROFILE_BADGES) },
                    onMyScenariosClick = { navController.navigate(Destinations.PROFILE_SCENARIOS) },
                    onSettingsClick = { navController.navigate(Destinations.SETTINGS) },
                    onHelpClick = { navController.navigate(Destinations.HELP) },
                    onLogoutClick = {
                        coroutineScope.launch {
                            app.authRepository.logout()
                            authViewModel.clearAuthentication()
                        }
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
                val vmState by profileViewModel.state.collectAsState()
                val profile = vmState.profile ?: sampleProfile
                ProfileProgressScreen(
                    profile = profile,
                    completedScenarios = completedScenarios,
                    totalScenarioCount = sampleScenarios.size,
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Destinations.PROFILE_BADGES) {
                val vmState by profileViewModel.state.collectAsState()
                val profile = vmState.profile ?: sampleProfile
                BadgeCollectionScreen(
                    completedScenarioCount = completedScenarioCategories.size,
                    streakDays = profile.streakDays,
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Destinations.PROFILE_SCENARIOS) {
                MyScenariosScreen(
                    completedScenarios = completedScenarios,
                    onScenarioClick = { scenario ->
                        val cat = scenario.category.ifEmpty { scenario.title }
                        navController.navigate(Destinations.scenarioDetailRoute(cat))
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Destinations.SETTINGS) {
                SettingsScreen(onBackClick = { navController.popBackStack() })
            }
            composable(Destinations.HELP) {
                HelpFeedbackScreen(onBackClick = { navController.popBackStack() })
            }
            composable(
                route = Destinations.SCENARIO_DETAIL,
                arguments = listOf(navArgument(Destinations.SCENARIO_CATEGORY_ARG) { type = NavType.StringType })
            ) { backStackEntry ->
                val encodedCategory = backStackEntry.arguments?.getString(Destinations.SCENARIO_CATEGORY_ARG) ?: ""
                val category = java.net.URLDecoder.decode(encodedCategory, "UTF-8")
                val uiState by scenariosViewModel.uiState.collectAsState()
                val scenario = uiState.scenarios.firstOrNull { it.category == category }
                if (scenario == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Dieses Szenario ist im Backend nicht verfuegbar.",
                            color = BrandBlue,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    ScenarioDetailScreen(
                        scenario = scenario,
                        isCompleted = category in completedScenarioCategories,
                        onStartClick = { navController.navigate(Destinations.scenarioPlayRoute(category)) },
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
            composable(
                route = Destinations.SCENARIO_PLAY,
                arguments = listOf(navArgument(Destinations.SCENARIO_CATEGORY_ARG) { type = NavType.StringType })
            ) { backStackEntry ->
                val encodedCategory = backStackEntry.arguments?.getString(Destinations.SCENARIO_CATEGORY_ARG) ?: ""
                val category = java.net.URLDecoder.decode(encodedCategory, "UTF-8")
                val playViewModel: ScenarioPlayViewModel = viewModel(
                    key = "play_$category",
                    factory = ScenarioPlayViewModel.factory(app.imageRepository, category)
                )
                ScenarioPlayScreen(
                    category = category,
                    viewModel = playViewModel,
                    onScenarioCompleted = { earnedPoints ->
                        sessionPoints += earnedPoints
                        if (category !in completedScenarioCategories) {
                            completedScenarioCategories.add(category)
                        }
                        homeViewModel.load()
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
        // Preview uses sample data; SsmobileApplication not available in preview
    }
}
