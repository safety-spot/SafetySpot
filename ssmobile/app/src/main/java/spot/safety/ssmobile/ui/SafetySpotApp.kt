package spot.safety.ssmobile.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import spot.safety.ssmobile.ui.components.SafetySpotBottomBar
import spot.safety.ssmobile.ui.home.HomeScreen
import spot.safety.ssmobile.ui.navigation.TopLevelDestination
import spot.safety.ssmobile.ui.profile.ProfileScreen
import spot.safety.ssmobile.ui.ranking.RankingScreen
import spot.safety.ssmobile.ui.scenario.ScenarioPlayScreen
import spot.safety.ssmobile.ui.scenarios.ScenariosScreen
import spot.safety.ssmobile.ui.theme.AppBackground
import spot.safety.ssmobile.ui.theme.SsmobileTheme

private sealed interface AppScreen {
    data object TopLevel : AppScreen
    data object ScenarioPlay : AppScreen
}

@Composable
fun SafetySpotApp(modifier: Modifier = Modifier) {
    var activeDestination by rememberSaveable { mutableStateOf(TopLevelDestination.HOME) }
    var appScreen by rememberSaveable { mutableStateOf<AppScreen>(AppScreen.TopLevel) }
    val showBottomBar = appScreen == AppScreen.TopLevel

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppBackground,
        bottomBar = {
            if (showBottomBar) {
                SafetySpotBottomBar(
                    activeDestination = activeDestination,
                    onDestinationSelected = {
                        activeDestination = it
                        appScreen = AppScreen.TopLevel
                    }
                )
            }
        }
    ) { innerPadding ->
        when (appScreen) {
            AppScreen.ScenarioPlay -> ScenarioPlayScreen(
                modifier = Modifier.padding(innerPadding),
                onBackClick = { appScreen = AppScreen.TopLevel }
            )

            AppScreen.TopLevel -> when (activeDestination) {
                TopLevelDestination.HOME -> HomeScreen(
                    modifier = Modifier.padding(innerPadding),
                    onShowAllCategories = { activeDestination = TopLevelDestination.SCENARIOS },
                    onContinueScenario = { appScreen = AppScreen.ScenarioPlay },
                    onCategoryClick = { activeDestination = TopLevelDestination.SCENARIOS }
                )

                TopLevelDestination.SCENARIOS -> ScenariosScreen(
                    modifier = Modifier.padding(innerPadding),
                    onScenarioClick = { appScreen = AppScreen.ScenarioPlay }
                )

                TopLevelDestination.RANKING -> RankingScreen(
                    modifier = Modifier.padding(innerPadding)
                )

                TopLevelDestination.PROFILE -> ProfileScreen(
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SafetySpotAppPreview() {
    SsmobileTheme {
        SafetySpotApp()
    }
}
