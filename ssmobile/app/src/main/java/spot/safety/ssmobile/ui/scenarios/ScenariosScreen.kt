package spot.safety.ssmobile.ui.scenarios

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import spot.safety.ssmobile.ui.components.FilterPill
import spot.safety.ssmobile.ui.components.SafetySearchBar
import spot.safety.ssmobile.ui.components.ScenarioCard
import spot.safety.ssmobile.ui.theme.AppBackground
import spot.safety.ssmobile.ui.theme.BrandBlue
import spot.safety.ssmobile.ui.theme.ChemieBlueSoft
import spot.safety.ssmobile.ui.theme.ChemieBlueTint
import spot.safety.ssmobile.ui.theme.DifficultyEasy
import spot.safety.ssmobile.ui.theme.DifficultyHard
import spot.safety.ssmobile.ui.theme.DifficultyMedium
import spot.safety.ssmobile.ui.theme.SportGreen
import spot.safety.ssmobile.ui.theme.SportGreenSoft
import spot.safety.ssmobile.ui.theme.SsmobileTheme
import spot.safety.ssmobile.ui.theme.TechnikPurple
import spot.safety.ssmobile.ui.theme.TechnikPurpleSoft
import spot.safety.ssmobile.ui.theme.TrafficRed
import spot.safety.ssmobile.ui.theme.WerkraumOrange
import spot.safety.ssmobile.ui.theme.WerkraumOrangeSoft

data class ScenarioUi(
    val id: Int,
    val title: String,
    val subtitle: String,
    val difficultyLabel: String,
    val difficultyColor: Color,
    val taskCount: Int,
    val isNew: Boolean,
    val accentColor: Color,
    val backgroundColor: Color,
    val iconText: String,
    val category: String = ""
)

private enum class ScenarioFilter(val label: String) {
    ALL("Alle"),
    NEW("Neu"),
    POPULAR("Beliebt"),
    COMPLETED("Abgeschlossen")
}

@Composable
fun ScenariosScreen(
    modifier: Modifier = Modifier,
    scenarios: List<ScenarioUi> = sampleScenarios,
    completedScenarioIds: Set<Int> = emptySet(),
    onScenarioClick: (ScenarioUi) -> Unit = {}
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableStateOf(ScenarioFilter.ALL) }

    val visibleScenarios = scenarios
        .filter { scenario ->
            searchQuery.isBlank() ||
                scenario.title.contains(searchQuery, ignoreCase = true) ||
                scenario.subtitle.contains(searchQuery, ignoreCase = true)
        }
        .filter { scenario ->
            when (selectedFilter) {
                ScenarioFilter.ALL -> true
                ScenarioFilter.NEW -> scenario.isNew
                ScenarioFilter.POPULAR -> scenario.taskCount >= 8
                ScenarioFilter.COMPLETED -> scenario.id in completedScenarioIds
            }
        }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp)
    ) {
        Text(text = "Szenarien", color = BrandBlue, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SafetySearchBar(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .padding(1.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "F", color = BrandBlue, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ScenarioFilter.entries.forEach { filter ->
                FilterPill(
                    text = filter.label,
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter }
                )
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            visibleScenarios.forEach { scenario ->
                ScenarioCard(
                    title = scenario.title,
                    subtitle = scenario.subtitle,
                    difficultyLabel = scenario.difficultyLabel,
                    difficultyColor = scenario.difficultyColor,
                    taskCount = scenario.taskCount,
                    isNew = scenario.isNew && scenario.id !in completedScenarioIds,
                    accentColor = scenario.accentColor,
                    backgroundColor = scenario.backgroundColor,
                    iconText = scenario.iconText,
                    onClick = { onScenarioClick(scenario) }
                )
            }
            if (visibleScenarios.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selectedFilter == ScenarioFilter.COMPLETED) {
                            "Noch keine Szenarien abgeschlossen."
                        } else {
                            "Keine Szenarien gefunden."
                        },
                        color = BrandBlue,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

val sampleScenarios = listOf(
    ScenarioUi(
        id = 1,
        title = "Chemieraum",
        subtitle = "Gefaehrliche Stoffe",
        difficultyLabel = "Mittel",
        difficultyColor = DifficultyMedium,
        taskCount = 10,
        isNew = true,
        accentColor = ChemieBlueTint,
        backgroundColor = ChemieBlueSoft,
        iconText = "C",
        category = "Chemieraum"
    ),
    ScenarioUi(
        id = 2,
        title = "Werkraum",
        subtitle = "Maschinen sicher nutzen",
        difficultyLabel = "Leicht",
        difficultyColor = DifficultyEasy,
        taskCount = 8,
        isNew = false,
        accentColor = WerkraumOrange,
        backgroundColor = WerkraumOrangeSoft,
        iconText = "W",
        category = "Werkraum"
    ),
    ScenarioUi(
        id = 3,
        title = "Sportunterricht",
        subtitle = "Verletzungsgefahr",
        difficultyLabel = "Leicht",
        difficultyColor = DifficultyEasy,
        taskCount = 7,
        isNew = false,
        accentColor = SportGreen,
        backgroundColor = SportGreenSoft,
        iconText = "S",
        category = "Sportunterricht"
    ),
    ScenarioUi(
        id = 4,
        title = "Strassenverkehr",
        subtitle = "Sicher unterwegs",
        difficultyLabel = "Mittel",
        difficultyColor = DifficultyMedium,
        taskCount = 9,
        isNew = false,
        accentColor = TrafficRed,
        backgroundColor = Color(0xFFFFEBEE),
        iconText = "V",
        category = "Strassenverkehr"
    ),
    ScenarioUi(
        id = 5,
        title = "Technikraum",
        subtitle = "Elektrische Geraete",
        difficultyLabel = "Schwer",
        difficultyColor = DifficultyHard,
        taskCount = 6,
        isNew = false,
        accentColor = TechnikPurple,
        backgroundColor = TechnikPurpleSoft,
        iconText = "T",
        category = "Technikraum"
    )
)

@Preview(showBackground = true)
@Composable
private fun ScenariosScreenPreview() {
    SsmobileTheme {
        ScenariosScreen()
    }
}
