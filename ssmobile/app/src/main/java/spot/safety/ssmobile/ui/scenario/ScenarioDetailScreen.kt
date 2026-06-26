package spot.safety.ssmobile.ui.scenario

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import spot.safety.ssmobile.R
import spot.safety.ssmobile.ui.components.DifficultyChip
import spot.safety.ssmobile.ui.components.SafetyProgressBar
import spot.safety.ssmobile.ui.scenarios.ScenarioUi
import spot.safety.ssmobile.ui.scenarios.sampleScenarios
import spot.safety.ssmobile.ui.theme.AppBackground
import spot.safety.ssmobile.ui.theme.BrandBlue
import spot.safety.ssmobile.ui.theme.BrandGreen
import spot.safety.ssmobile.ui.theme.CardBorder
import spot.safety.ssmobile.ui.theme.MutedText
import spot.safety.ssmobile.ui.theme.PointsYellow
import spot.safety.ssmobile.ui.theme.SsmobileTheme

@Composable
fun ScenarioDetailScreen(
    modifier: Modifier = Modifier,
    scenario: ScenarioUi,
    isCompleted: Boolean,
    onStartClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp)
    ) {
        ScenarioDetailHeader(onBackClick = onBackClick)
        Spacer(modifier = Modifier.height(16.dp))
        ScenarioHero(scenario = scenario, isCompleted = isCompleted)
        Spacer(modifier = Modifier.height(16.dp))
        DetailStatsRow(scenario = scenario, isCompleted = isCompleted)
        Spacer(modifier = Modifier.height(16.dp))
        LearningGoalsCard(scenario = scenario)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onStartClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(99.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
        ) {
            Text(
                text = if (isCompleted) "Nochmal spielen" else "Szenario starten",
                style = MaterialTheme.typography.labelLarge
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun ScenarioDetailHeader(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .clickable(onClick = onBackClick),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "<", color = BrandBlue, style = MaterialTheme.typography.titleMedium)
        }
        Text(
            text = "Szenario",
            color = BrandBlue,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 14.dp)
        )
    }
}

@Composable
private fun ScenarioHero(
    scenario: ScenarioUi,
    isCompleted: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(scenario.backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    val categoryImageResId = when {
                        scenario.category.isChemistryCategory() || scenario.title.isChemistryCategory() ->
                            R.drawable.category_chemistry
                        scenario.category.isSportsCategory() || scenario.title.isSportsCategory() ->
                            R.drawable.category_sports
                        scenario.category.isTechnologyCategory() || scenario.title.isTechnologyCategory() ->
                            R.drawable.category_technology
                        scenario.category.isTrafficCategory() || scenario.title.isTrafficCategory() ->
                            R.drawable.category_traffic
                        else -> null
                    }
                    if (categoryImageResId != null) {
                        Image(
                            painter = painterResource(id = categoryImageResId),
                            contentDescription = scenario.title,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Text(
                            text = scenario.iconText,
                            color = scenario.accentColor,
                            style = MaterialTheme.typography.displaySmall
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                DifficultyChip(label = scenario.difficultyLabel, color = scenario.difficultyColor)
                Spacer(modifier = Modifier.weight(1f))
                CompletionBadge(isCompleted = isCompleted)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = scenario.title, color = BrandBlue, style = MaterialTheme.typography.displaySmall)
            Text(text = scenario.subtitle, color = MutedText, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            SafetyProgressBar(progress = if (isCompleted) 1f else 0f)
        }
    }
}

@Composable
private fun CompletionBadge(isCompleted: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(if (isCompleted) BrandGreen.copy(alpha = 0.14f) else PointsYellow.copy(alpha = 0.18f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isCompleted) "Abgeschlossen" else "Offen",
            color = if (isCompleted) BrandGreen else PointsYellow,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun DetailStatsRow(
    scenario: ScenarioUi,
    isCompleted: Boolean
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        DetailStat(
            value = scenario.taskCount.toString(),
            label = "Aufgaben",
            modifier = Modifier.weight(1f)
        )
        DetailStat(
            value = if (isCompleted) "100 %" else "0 %",
            label = "Fortschritt",
            modifier = Modifier.weight(1f)
        )
        DetailStat(
            value = "40",
            label = "Punkte je Aufgabe",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun DetailStat(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(96.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = value, color = BrandBlue, style = MaterialTheme.typography.titleMedium)
            Text(text = label, color = MutedText, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun LearningGoalsCard(scenario: ScenarioUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Lernziele", color = BrandBlue, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(12.dp))
            goalTextsForScenario(scenario.id).forEach { goal ->
                GoalRow(text = goal)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun GoalRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(BrandGreen)
        )
        Text(
            text = text,
            color = BrandBlue,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 10.dp)
        )
    }
}

private fun goalTextsForScenario(scenarioId: Int): List<String> = when (scenarioId) {
    2 -> listOf(
        "Werkzeuge und Maschinen sicher vorbereiten",
        "Schutzkleidung in typischen Situationen erkennen",
        "Gefaehrliche Arbeitsweisen einschaetzen"
    )

    3 -> listOf(
        "Verletzungsrisiken im Sportunterricht erkennen",
        "Sichere Vorbereitung von Uebungen bewerten",
        "Rutsch- und Sturzgefahren einschaetzen"
    )

    4 -> listOf(
        "Sichere Entscheidungen im Strassenverkehr treffen",
        "Sichtbarkeit und Aufmerksamkeit bewerten",
        "Riskante Wege ueber die Strasse erkennen"
    )

    5 -> listOf(
        "Defekte elektrische Geraete erkennen",
        "Sichere Handgriffe im Technikraum bewerten",
        "Strom- und Brandgefahren einschaetzen"
    )

    else -> listOf(
        "Gefaehrliche Stoffe im Chemieraum erkennen",
        "Schutzmassnahmen richtig einschaetzen",
        "Unsichere Handlungen vor dem Experiment vermeiden"
    )
}

private fun String.isChemistryCategory(): Boolean =
    contains("chem", ignoreCase = true) || contains("chemie", ignoreCase = true)

private fun String.isSportsCategory(): Boolean =
    contains("sport", ignoreCase = true)

private fun String.isTechnologyCategory(): Boolean =
    contains("tech", ignoreCase = true)

private fun String.isTrafficCategory(): Boolean =
    contains("traffic", ignoreCase = true) ||
        contains("verkehr", ignoreCase = true) ||
        contains("strass", ignoreCase = true)

@Preview(showBackground = true)
@Composable
private fun ScenarioDetailScreenPreview() {
    SsmobileTheme {
        ScenarioDetailScreen(
            scenario = sampleScenarios.first(),
            isCompleted = false,
            onStartClick = {},
            onBackClick = {}
        )
    }
}
