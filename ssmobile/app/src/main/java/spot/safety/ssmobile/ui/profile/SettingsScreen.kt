package spot.safety.ssmobile.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import spot.safety.ssmobile.ui.theme.AppBackground
import spot.safety.ssmobile.ui.theme.BrandBlue
import spot.safety.ssmobile.ui.theme.BrandGreen
import spot.safety.ssmobile.ui.theme.CardBorder
import spot.safety.ssmobile.ui.theme.MutedText
import spot.safety.ssmobile.ui.theme.SsmobileTheme

private enum class TextSizeOption(val label: String) {
    COMPACT("Kompakt"),
    NORMAL("Normal"),
    LARGE("Gross")
}

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    var dailyReminderEnabled by rememberSaveable { mutableStateOf(true) }
    var soundEnabled by rememberSaveable { mutableStateOf(false) }
    var learningHintsEnabled by rememberSaveable { mutableStateOf(true) }
    var selectedTextSize by rememberSaveable { mutableStateOf(TextSizeOption.NORMAL) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp)
    ) {
        SettingsHeader(onBackClick = onBackClick)
        Spacer(modifier = Modifier.height(18.dp))
        SettingsGroup(title = "Lernen") {
            SettingsSwitchRow(
                title = "Taegliche Erinnerung",
                subtitle = "Kurzer Hinweis fuer deine SafetySpot-Serie",
                checked = dailyReminderEnabled,
                onCheckedChange = { dailyReminderEnabled = it }
            )
            SettingsDivider()
            SettingsSwitchRow(
                title = "Lernhinweise",
                subtitle = "Tipps nach falschen Antworten anzeigen",
                checked = learningHintsEnabled,
                onCheckedChange = { learningHintsEnabled = it }
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        SettingsGroup(title = "App") {
            SettingsSwitchRow(
                title = "Sounds",
                subtitle = "Feedback-Toene im Szenario",
                checked = soundEnabled,
                onCheckedChange = { soundEnabled = it }
            )
            SettingsDivider()
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Textgroesse", color = BrandBlue, style = MaterialTheme.typography.titleMedium)
                Text(text = selectedTextSize.label, color = MutedText, style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextSizeOption.entries.forEach { option ->
                        FilterPill(
                            text = option.label,
                            selected = selectedTextSize == option,
                            onClick = { selectedTextSize = option },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        SettingsGroup(title = "Konto") {
            SettingsInfoRow(title = "Datenschutz", value = "Lokal vorbereitet")
            SettingsDivider()
            SettingsInfoRow(title = "Version", value = "Frontend WIP")
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun SettingsHeader(onBackClick: () -> Unit) {
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
            text = "Einstellungen",
            color = BrandBlue,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 14.dp)
        )
    }
}

@Composable
private fun SettingsGroup(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(text = title, color = BrandBlue, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = BrandBlue, style = MaterialTheme.typography.titleMedium)
            Text(text = subtitle, color = MutedText, style = MaterialTheme.typography.labelMedium)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BrandGreen)
        )
    }
}

@Composable
private fun SettingsInfoRow(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = BrandBlue, style = MaterialTheme.typography.titleMedium)
        Text(text = value, color = MutedText, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(CardBorder)
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    SsmobileTheme {
        SettingsScreen()
    }
}
