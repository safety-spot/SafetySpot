package spot.safety.ssmobile.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import spot.safety.ssmobile.ui.theme.SafeGreenBg
import spot.safety.ssmobile.ui.theme.SsmobileTheme
import spot.safety.ssmobile.ui.theme.TrafficRed
import spot.safety.ssmobile.util.formatScore

@Composable
fun ProfileProgressScreen(
    profile: ProfileUi,
    completedScenarios: List<ScenarioUi>,
    totalScenarioCount: Int,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    ProfileInsightScaffold(
        title = "Mein Fortschritt",
        subtitle = "Dein aktueller Lernstand in SafetySpot.",
        onBackClick = onBackClick,
        modifier = modifier
    ) {
        ProgressSummaryCard(profile = profile, completedScenarios = completedScenarios, totalScenarioCount = totalScenarioCount)
        Spacer(modifier = Modifier.height(14.dp))
        Text(text = "Lernziele", color = BrandBlue, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ProgressGoalRow(
                title = "Szenarien abschliessen",
                value = "${completedScenarios.size} / $totalScenarioCount",
                progress = completedScenarios.size.toFloat() / totalScenarioCount.toFloat()
            )
            ProgressGoalRow(
                title = "Naechstes Level",
                value = "${formatScore(profile.currentXp)} / ${formatScore(profile.nextLevelXp)} XP",
                progress = profile.currentXp.toFloat() / profile.nextLevelXp.toFloat()
            )
            ProgressGoalRow(
                title = "Streak halten",
                value = "${profile.streakDays} Tage",
                progress = (profile.streakDays / 7f).coerceAtMost(1f)
            )
        }
    }
}

@Composable
fun BadgeCollectionScreen(
    completedScenarioCount: Int,
    streakDays: Int,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    val badges = listOf(
        BadgeUi("Erste Aufgabe", "Eine Aufgabe bewertet", true),
        BadgeUi("7 Tage Streak", "Eine Woche aktiv geblieben", streakDays >= 7),
        BadgeUi("Szenario-Profi", "Erstes Szenario abgeschlossen", completedScenarioCount >= 1),
        BadgeUi("Kategorie-Scout", "Drei Szenarien abgeschlossen", completedScenarioCount >= 3),
        BadgeUi("Safety Champion", "Alle Mock-Szenarien abgeschlossen", completedScenarioCount >= sampleScenarios.size)
    )

    ProfileInsightScaffold(
        title = "Abzeichen",
        subtitle = "Deine Erfolge und naechsten Meilensteine.",
        onBackClick = onBackClick,
        modifier = modifier
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            badges.forEach { badge ->
                BadgeRow(badge = badge)
            }
        }
    }
}

@Composable
fun MyScenariosScreen(
    completedScenarios: List<ScenarioUi>,
    modifier: Modifier = Modifier,
    onScenarioClick: (ScenarioUi) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    ProfileInsightScaffold(
        title = "Meine Szenarien",
        subtitle = "Abgeschlossene Szenarien bleiben hier sichtbar.",
        onBackClick = onBackClick,
        modifier = modifier
    ) {
        if (completedScenarios.isEmpty()) {
            EmptyScenariosCard()
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                completedScenarios.forEach { scenario ->
                    CompletedScenarioRow(
                        scenario = scenario,
                        onClick = { onScenarioClick(scenario) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileInsightScaffold(
    title: String,
    subtitle: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp)
    ) {
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
                text = title,
                color = BrandBlue,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(start = 14.dp)
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(text = subtitle, color = MutedText, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(18.dp))
        content()
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun ProgressSummaryCard(
    profile: ProfileUi,
    completedScenarios: List<ScenarioUi>,
    totalScenarioCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Level ${profile.level}", color = BrandBlue, style = MaterialTheme.typography.titleLarge)
            Text(text = "${formatScore(profile.points)} Punkte", color = PointsYellow, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(14.dp))
            SafetyProgressBar(progress = completedScenarios.size.toFloat() / totalScenarioCount.toFloat())
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${completedScenarios.size} von $totalScenarioCount Szenarien abgeschlossen",
                color = MutedText,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun ProgressGoalRow(
    title: String,
    value: String,
    progress: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, color = BrandBlue, style = MaterialTheme.typography.titleMedium)
                Text(text = value, color = MutedText, style = MaterialTheme.typography.labelMedium)
            }
            Spacer(modifier = Modifier.height(10.dp))
            SafetyProgressBar(progress = progress)
        }
    }
}

private data class BadgeUi(
    val title: String,
    val subtitle: String,
    val unlocked: Boolean
)

@Composable
private fun BadgeRow(badge: BadgeUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (badge.unlocked) SafeGreenBg else Color(0xFFE7ECF2)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (badge.unlocked) "OK" else "?",
                    color = if (badge.unlocked) BrandGreen else MutedText,
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(text = badge.title, color = BrandBlue, style = MaterialTheme.typography.titleMedium)
                Text(text = badge.subtitle, color = MutedText, style = MaterialTheme.typography.labelMedium)
            }
            Text(
                text = if (badge.unlocked) "Aktiv" else "Offen",
                color = if (badge.unlocked) BrandGreen else TrafficRed,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun CompletedScenarioRow(
    scenario: ScenarioUi,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(scenario.backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Text(text = scenario.iconText, color = scenario.accentColor, style = MaterialTheme.typography.titleMedium)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(text = scenario.title, color = BrandBlue, style = MaterialTheme.typography.titleMedium)
                Text(text = scenario.subtitle, color = MutedText, style = MaterialTheme.typography.labelMedium)
            }
            DifficultyChip(label = scenario.difficultyLabel, color = scenario.difficultyColor)
        }
    }
}

@Composable
private fun EmptyScenariosCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Noch nichts abgeschlossen", color = BrandBlue, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Starte ein Szenario, dann erscheint es hier.",
                color = MutedText,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileProgressScreenPreview() {
    SsmobileTheme {
        ProfileProgressScreen(
            profile = sampleProfile,
            completedScenarios = sampleScenarios.take(1),
            totalScenarioCount = sampleScenarios.size
        )
    }
}
