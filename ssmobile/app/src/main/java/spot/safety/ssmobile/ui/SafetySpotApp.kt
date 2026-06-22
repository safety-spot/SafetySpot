package spot.safety.ssmobile.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import spot.safety.ssmobile.ui.auth.AuthScreen
import spot.safety.ssmobile.ui.components.SafetySpotBottomBar
import spot.safety.ssmobile.ui.home.HomeScreen
import spot.safety.ssmobile.ui.navigation.Destinations
import spot.safety.ssmobile.ui.navigation.TopLevelDestination
import spot.safety.ssmobile.ui.profile.ProfileScreen
import spot.safety.ssmobile.ui.ranking.RankingScreen
import spot.safety.ssmobile.ui.scenario.ScenarioPlayScreen
import spot.safety.ssmobile.ui.scenarios.ScenariosScreen
import spot.safety.ssmobile.ui.theme.AppBackground
import spot.safety.ssmobile.ui.theme.SsmobileTheme

@Composable
fun SafetySpotApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
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
                    onShowAllCategories = { navController.navigateToTopLevel(TopLevelDestination.SCENARIOS) },
                    onContinueScenario = { navController.navigate(Destinations.SCENARIO_PLAY) },
                    onCategoryClick = { navController.navigateToTopLevel(TopLevelDestination.SCENARIOS) }
                )
            }
            composable(Destinations.SCENARIOS) {
                ScenariosScreen(
                    onScenarioClick = { navController.navigate(Destinations.SCENARIO_PLAY) }
                )
            }
            composable(Destinations.RANKING) {
                RankingScreen()
            }
            composable(Destinations.PROFILE) {
                ProfileScreen(
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
            composable(Destinations.SCENARIO_PLAY) {
                ScenarioPlayScreen(
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
