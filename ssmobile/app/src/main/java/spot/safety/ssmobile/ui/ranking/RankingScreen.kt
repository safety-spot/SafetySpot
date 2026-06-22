package spot.safety.ssmobile.ui.ranking

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import spot.safety.ssmobile.ui.components.FilterPill
import spot.safety.ssmobile.ui.theme.AppBackground
import spot.safety.ssmobile.ui.theme.BrandBlue
import spot.safety.ssmobile.ui.theme.BrandGreen
import spot.safety.ssmobile.ui.theme.CardBorder
import spot.safety.ssmobile.ui.theme.MutedText
import spot.safety.ssmobile.ui.theme.PointsYellow
import spot.safety.ssmobile.ui.theme.SsmobileTheme
import spot.safety.ssmobile.util.formatScore

data class LeaderboardEntryUi(
    val rank: Int,
    val name: String,
    val score: Int,
    val avatarText: String,
    val isCurrentUser: Boolean = false
)

private enum class RankingScope(val label: String) {
    CLASS("Klasse"),
    SCHOOL("Schule"),
    WORLD("Weltweit")
}

@Composable
fun RankingScreen(
    modifier: Modifier = Modifier,
    entries: List<LeaderboardEntryUi> = sampleLeaderboard
) {
    var selectedScope by rememberSaveable { mutableStateOf(RankingScope.CLASS) }
    val podiumEntries = entries.take(3)
    val listEntries = entries.drop(3)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp)
    ) {
        Text(text = "Ranking", color = BrandBlue, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            RankingScope.entries.forEach { scope ->
                FilterPill(
                    text = scope.label,
                    selected = selectedScope == scope,
                    onClick = { selectedScope = scope },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Spacer(modifier = Modifier.height(22.dp))
        Podium(entries = podiumEntries)
        Spacer(modifier = Modifier.height(20.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            listEntries.forEach { entry ->
                LeaderboardRow(entry = entry)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun Podium(entries: List<LeaderboardEntryUi>) {
    val second = entries.getOrNull(1)
    val first = entries.getOrNull(0)
    val third = entries.getOrNull(2)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        second?.let {
            PodiumColumn(
                entry = it,
                height = 142.dp,
                color = Color(0xFFE8EDF3),
                modifier = Modifier.weight(1f)
            )
        }
        first?.let {
            PodiumColumn(
                entry = it,
                height = 178.dp,
                color = Color(0xFFFFF2C2),
                modifier = Modifier.weight(1f)
            )
        }
        third?.let {
            PodiumColumn(
                entry = it,
                height = 120.dp,
                color = Color(0xFFFFE2C7),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PodiumColumn(
    entry: LeaderboardEntryUi,
    height: Dp,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(if (entry.rank == 1) PointsYellow else Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = entry.rank.toString(),
                color = BrandBlue,
                style = MaterialTheme.typography.labelLarge
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Avatar(text = entry.avatarText, size = 52.dp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = entry.name, color = BrandBlue, style = MaterialTheme.typography.labelLarge)
        Text(
            text = formatScore(entry.score),
            color = MutedText,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun LeaderboardRow(entry: LeaderboardEntryUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (entry.isCurrentUser) BrandGreen.copy(alpha = 0.12f) else Color.White
        ),
        border = if (entry.isCurrentUser) CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(BrandGreen.copy(alpha = 0.35f))
        ) else CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = entry.rank.toString(),
                color = if (entry.isCurrentUser) BrandGreen else MutedText,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.width(28.dp)
            )
            Avatar(text = entry.avatarText, size = 40.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = entry.name,
                color = BrandBlue,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = formatScore(entry.score),
                color = if (entry.isCurrentUser) BrandGreen else MutedText,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun Avatar(text: String, size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(BrandBlue.copy(alpha = 0.10f)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = BrandBlue, style = MaterialTheme.typography.labelLarge)
    }
}

val sampleLeaderboard = listOf(
    LeaderboardEntryUi(1, "Lena", 3250, "L"),
    LeaderboardEntryUi(2, "Max", 2450, "M"),
    LeaderboardEntryUi(3, "Emre", 2150, "E"),
    LeaderboardEntryUi(4, "Sophie", 1980, "S"),
    LeaderboardEntryUi(5, "Jonas", 1720, "J"),
    LeaderboardEntryUi(6, "Du (Max)", 1450, "M", isCurrentUser = true),
    LeaderboardEntryUi(7, "Mia", 1230, "M"),
    LeaderboardEntryUi(8, "Paul", 980, "P")
)

@Preview(showBackground = true)
@Composable
private fun RankingScreenPreview() {
    SsmobileTheme {
        RankingScreen()
    }
}
