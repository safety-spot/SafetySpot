package spot.safety.ssmobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import spot.safety.ssmobile.ui.navigation.TopLevelDestination
import spot.safety.ssmobile.ui.theme.BrandBlue
import spot.safety.ssmobile.ui.theme.BrandGreen
import spot.safety.ssmobile.ui.theme.CardBorder
import spot.safety.ssmobile.ui.theme.ChemieBlueSoft
import spot.safety.ssmobile.ui.theme.ChemieBlueTint
import spot.safety.ssmobile.ui.theme.DifficultyEasy
import spot.safety.ssmobile.ui.theme.DifficultyHard
import spot.safety.ssmobile.ui.theme.DifficultyMedium
import spot.safety.ssmobile.ui.theme.MutedText
import spot.safety.ssmobile.ui.theme.PointsYellow
import spot.safety.ssmobile.ui.theme.SsmobileTheme
import spot.safety.ssmobile.util.formatScore

@Composable
fun MetricCard(
    iconText: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    iconBackground: Color = BrandGreen,
    iconColor: Color = Color.White
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iconText,
                    color = iconColor,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, color = BrandBlue, style = MaterialTheme.typography.titleMedium)
            Text(text = label, color = MutedText, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun StreakRow(streakDays: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Streak", color = BrandBlue, style = MaterialTheme.typography.labelLarge)
        Text(
            text = "$streakDays Tage in Folge",
            color = MutedText,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun SafetyProgressBar(progress: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(8.dp)
            .clip(RoundedCornerShape(99.dp))
            .background(Color(0xFFE7ECF2))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(8.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(BrandGreen)
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    actionLabel: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = BrandBlue, style = MaterialTheme.typography.titleLarge)
        Text(
            text = actionLabel,
            color = BrandGreen,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.clickable(onClick = onActionClick)
        )
    }
}

@Composable
fun FilterPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 36.dp)
            .clip(RoundedCornerShape(99.dp))
            .background(if (selected) BrandGreen else Color(0xFFEFF3F7))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else MutedText,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun SafetySearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = {
            Text(text = "Suchen...", color = MutedText, style = MaterialTheme.typography.bodyMedium)
        },
        leadingIcon = {
            Text(text = "S", color = MutedText, style = MaterialTheme.typography.labelLarge)
        },
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        textStyle = MaterialTheme.typography.bodyMedium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BrandGreen,
            unfocusedBorderColor = CardBorder,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Composable
fun DifficultyChip(
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(99.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = color, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun TagChip(
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(99.dp))
            .background(BrandGreen.copy(alpha = 0.12f))
            .padding(horizontal = 9.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = BrandGreen, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun ScenarioCard(
    title: String,
    subtitle: String,
    difficultyLabel: String,
    difficultyColor: Color,
    taskCount: Int,
    isNew: Boolean,
    accentColor: Color,
    backgroundColor: Color,
    iconText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Text(text = iconText, color = accentColor, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        color = BrandBlue,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (isNew) {
                        Spacer(modifier = Modifier.width(8.dp))
                        TagChip(label = "Neu")
                    }
                }
                Text(
                    text = subtitle,
                    color = MutedText,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DifficultyChip(label = difficultyLabel, color = difficultyColor)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "$taskCount Aufgaben",
                        color = MutedText,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
fun ContinueBanner(
    title: String,
    subtitle: String,
    progress: Float,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(82.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ChemieBlueSoft),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Becher", color = ChemieBlueTint, style = MaterialTheme.typography.labelLarge)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = BrandBlue, style = MaterialTheme.typography.titleMedium)
                Text(text = subtitle, color = MutedText, style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SafetyProgressBar(progress = progress, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${(progress * 100).toInt()} %",
                        color = MutedText,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                onClick = onContinueClick,
                shape = RoundedCornerShape(99.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                contentPadding = ButtonDefaults.ContentPadding
            ) {
                Text(text = "Fortsetzen", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
fun CategoryTile(
    name: String,
    scenarioCount: Int,
    accentColor: Color,
    backgroundColor: Color,
    iconText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.55f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = iconText, color = accentColor, style = MaterialTheme.typography.labelLarge)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = name, color = BrandBlue, style = MaterialTheme.typography.labelLarge)
            Text(
                text = "$scenarioCount Szenarien",
                color = MutedText,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
fun SafetySpotBottomBar(
    activeDestination: TopLevelDestination,
    onDestinationSelected: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 76.dp)
            .background(Color.White)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TopLevelDestination.entries.forEach { destination ->
            BottomBarItem(
                label = destination.label,
                iconRes = destination.iconRes,
                active = destination == activeDestination,
                onClick = { onDestinationSelected(destination) }
            )
        }
    }
}

@Composable
private fun BottomBarItem(
    label: String,
    iconRes: Int,
    active: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (active) BrandGreen else Color(0xFFE3E8EF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                tint = if (active) Color.White else MutedText,
                modifier = Modifier.size(bottomBarIconSize(label))
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = if (active) BrandGreen else MutedText,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

private fun bottomBarIconSize(label: String): Dp = when (label) {
    "Szenarien" -> 21.dp
    else -> 20.dp
}

@Preview(showBackground = true)
@Composable
private fun HomeComponentsPreview() {
    SsmobileTheme {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard("12", "Level", "Sicherheitsprofi")
            StreakRow(streakDays = 7)
            ContinueBanner("Chemieraum", "Gefaehrliche Stoffe", 0.6f, {})
            CategoryTile("Chemie", 8, ChemieBlueTint, ChemieBlueSoft, "C", {})
            ScenarioCard(
                title = "Chemieraum",
                subtitle = "Gefaehrliche Stoffe",
                difficultyLabel = "Mittel",
                difficultyColor = DifficultyMedium,
                taskCount = 10,
                isNew = true,
                accentColor = ChemieBlueTint,
                backgroundColor = ChemieBlueSoft,
                iconText = "C",
                onClick = {}
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterPill("Alle", true, {})
                FilterPill("Neu", false, {})
                DifficultyChip("Leicht", DifficultyEasy)
                DifficultyChip("Schwer", DifficultyHard)
            }
            SafetySpotBottomBar(TopLevelDestination.HOME, {})
            Text(text = formatScore(2450), color = PointsYellow)
        }
    }
}
