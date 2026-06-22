package spot.safety.ssmobile.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import spot.safety.ssmobile.ui.components.CategoryTile
import spot.safety.ssmobile.ui.components.ContinueBanner
import spot.safety.ssmobile.ui.components.MetricCard
import spot.safety.ssmobile.ui.components.SectionHeader
import spot.safety.ssmobile.ui.components.StreakRow
import spot.safety.ssmobile.ui.theme.AppBackground
import spot.safety.ssmobile.ui.theme.BrandBlue
import spot.safety.ssmobile.ui.theme.BrandGreen
import spot.safety.ssmobile.ui.theme.ChemieBlueSoft
import spot.safety.ssmobile.ui.theme.ChemieBlueTint
import spot.safety.ssmobile.ui.theme.PointsYellow
import spot.safety.ssmobile.ui.theme.SportGreen
import spot.safety.ssmobile.ui.theme.SportGreenSoft
import spot.safety.ssmobile.ui.theme.SsmobileTheme
import spot.safety.ssmobile.ui.theme.TechnikPurple
import spot.safety.ssmobile.ui.theme.TechnikPurpleSoft
import spot.safety.ssmobile.ui.theme.WerkraumOrange
import spot.safety.ssmobile.ui.theme.WerkraumOrangeSoft
import spot.safety.ssmobile.util.formatScore

data class HomeCategoryUi(
    val name: String,
    val scenarioCount: Int,
    val accentColor: Color,
    val backgroundColor: Color,
    val iconText: String
)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    displayName: String = "Max",
    level: Int = 12,
    levelTitle: String = "Sicherheitsprofi",
    points: Int = 2450,
    streakDays: Int = 7,
    completedScenarioCount: Int = 0,
    categories: List<HomeCategoryUi> = sampleHomeCategories,
    onShowAllCategories: () -> Unit = {},
    onContinueScenario: () -> Unit = {},
    onCategoryClick: (HomeCategoryUi) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp)
    ) {
        HomeHeader(displayName = displayName)
        Spacer(modifier = Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            MetricCard(
                iconText = level.toString(),
                value = "Level",
                label = levelTitle,
                modifier = Modifier.weight(1f),
                iconBackground = BrandGreen
            )
            MetricCard(
                iconText = "*",
                value = formatScore(points),
                label = "Punkte",
                modifier = Modifier.weight(1f),
                iconBackground = PointsYellow,
                iconColor = BrandBlue
            )
        }
        StreakRow(streakDays = streakDays)
        Spacer(modifier = Modifier.height(22.dp))
        Text(text = "Weitermachen", color = BrandBlue, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(10.dp))
        ContinueBanner(
            title = if (completedScenarioCount == 0) "Chemieraum" else "Naechstes Szenario",
            subtitle = if (completedScenarioCount == 0) "Gefaehrliche Stoffe" else "$completedScenarioCount abgeschlossen",
            progress = if (completedScenarioCount == 0) 0.6f else 0.15f,
            onContinueClick = onContinueScenario
        )
        Spacer(modifier = Modifier.height(22.dp))
        SectionHeader(
            title = "Kategorien",
            actionLabel = "Alle anzeigen",
            onActionClick = onShowAllCategories
        )
        Spacer(modifier = Modifier.height(10.dp))
        categories.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowItems.forEach { category ->
                    CategoryTile(
                        name = category.name,
                        scenarioCount = category.scenarioCount,
                        accentColor = category.accentColor,
                        backgroundColor = category.backgroundColor,
                        iconText = category.iconText,
                        onClick = { onCategoryClick(category) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun HomeHeader(displayName: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Hallo, $displayName!",
            color = BrandBlue,
            style = MaterialTheme.typography.headlineSmall
        )
        Text(text = "!", color = BrandBlue, style = MaterialTheme.typography.titleLarge)
    }
}

val sampleHomeCategories = listOf(
    HomeCategoryUi("Chemie", 8, ChemieBlueTint, ChemieBlueSoft, "C"),
    HomeCategoryUi("Werkraum", 6, WerkraumOrange, WerkraumOrangeSoft, "W"),
    HomeCategoryUi("Sport", 7, SportGreen, SportGreenSoft, "S"),
    HomeCategoryUi("Technik", 5, TechnikPurple, TechnikPurpleSoft, "T")
)

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    SsmobileTheme {
        HomeScreen()
    }
}
