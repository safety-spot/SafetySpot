package spot.safety.ssmobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import spot.safety.ssmobile.ui.components.SafetySpotBottomBar
import spot.safety.ssmobile.ui.home.HomeScreen
import spot.safety.ssmobile.ui.navigation.TopLevelDestination
import spot.safety.ssmobile.ui.profile.ProfileScreen
import spot.safety.ssmobile.ui.ranking.RankingScreen
import spot.safety.ssmobile.ui.scenarios.ScenariosScreen
import spot.safety.ssmobile.ui.theme.AppBackground
import spot.safety.ssmobile.ui.theme.BrandBlue
import spot.safety.ssmobile.ui.theme.MutedText
import spot.safety.ssmobile.ui.theme.SsmobileTheme

@Composable
fun SafetySpotApp(modifier: Modifier = Modifier) {
    var activeDestination by rememberSaveable { mutableStateOf(TopLevelDestination.HOME) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppBackground,
        bottomBar = {
            SafetySpotBottomBar(
                activeDestination = activeDestination,
                onDestinationSelected = { activeDestination = it }
            )
        }
    ) { innerPadding ->
        when (activeDestination) {
            TopLevelDestination.HOME -> HomeScreen(
                modifier = Modifier.padding(innerPadding),
                onShowAllCategories = { activeDestination = TopLevelDestination.SCENARIOS },
                onCategoryClick = { activeDestination = TopLevelDestination.SCENARIOS }
            )

            TopLevelDestination.SCENARIOS -> ScenariosScreen(
                modifier = Modifier.padding(innerPadding)
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

@Composable
private fun PlaceholderTopLevelScreen(
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, color = BrandBlue, style = MaterialTheme.typography.headlineSmall)
        Text(
            text = message,
            color = MutedText,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SafetySpotAppPreview() {
    SsmobileTheme {
        SafetySpotApp()
    }
}
