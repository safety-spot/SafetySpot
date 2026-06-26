package spot.safety.ssmobile.ui.profile

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import spot.safety.ssmobile.R
import spot.safety.ssmobile.ui.components.SafetyProgressBar
import spot.safety.ssmobile.ui.theme.AppBackground
import spot.safety.ssmobile.ui.theme.BrandBlue
import spot.safety.ssmobile.ui.theme.BrandGreen
import spot.safety.ssmobile.ui.theme.CardBorder
import spot.safety.ssmobile.ui.theme.MutedText
import spot.safety.ssmobile.ui.theme.SsmobileTheme
import spot.safety.ssmobile.ui.theme.TrafficRed
import spot.safety.ssmobile.util.formatScore

data class ProfileUi(
    val fullName: String,
    val level: Int,
    val currentXp: Int,
    val nextLevelXp: Int,
    val points: Int,
    val streakDays: Int,
    val badges: Int
)

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    profile: ProfileUi = sampleProfile,
    profileViewModel: ProfileViewModel? = null,
    onProgressClick: () -> Unit = {},
    onBadgesClick: () -> Unit = {},
    onMyScenariosClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onHelpClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val vmState by (profileViewModel?.state ?: MutableStateFlow(ProfileViewState(isLoading = false))).collectAsState()
    val displayProfile = vmState.profile ?: profile
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp)
    ) {
        ProfileHeader()
        Spacer(modifier = Modifier.height(18.dp))
        ProfileCard(profile = displayProfile)
        Spacer(modifier = Modifier.height(18.dp))
        ProfileMenuGroup(
            items = listOf(
                ProfileMenuItem("Mein Fortschritt", onProgressClick),
                ProfileMenuItem("Abzeichen", onBadgesClick),
                ProfileMenuItem("Meine Szenarien", onMyScenariosClick)
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        ProfileMenuGroup(
            items = listOf(
                ProfileMenuItem("Einstellungen", onSettingsClick),
                ProfileMenuItem("Hilfe & Feedback", onHelpClick)
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        LogoutRow(onClick = onLogoutClick)
        Spacer(modifier = Modifier.height(12.dp))
    }
}

private data class ProfileMenuItem(
    val label: String,
    val onClick: () -> Unit
)

@Composable
private fun ProfileHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Profil", color = BrandBlue, style = MaterialTheme.typography.headlineSmall)
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "E", color = BrandBlue, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun ProfileCard(profile: ProfileUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(86.dp)
                    .clip(CircleShape)
                    .background(BrandGreen.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_profile_avatar),
                    contentDescription = profile.fullName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = profile.fullName, color = BrandBlue, style = MaterialTheme.typography.titleLarge)
            Text(
                text = "Level ${profile.level}",
                color = BrandGreen,
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.height(14.dp))
            SafetyProgressBar(
                progress = profile.currentXp.toFloat() / profile.nextLevelXp.toFloat(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${formatScore(profile.currentXp)} / ${formatScore(profile.nextLevelXp)} XP",
                color = MutedText,
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.height(18.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                ProfileStat(value = formatScore(profile.points), label = "Punkte", modifier = Modifier.weight(1f))
                ProfileStat(value = profile.streakDays.toString(), label = "Tage Streak", modifier = Modifier.weight(1f))
                ProfileStat(value = profile.badges.toString(), label = "Abzeichen", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ProfileStat(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = BrandBlue, style = MaterialTheme.typography.titleMedium)
        Text(text = label, color = MutedText, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun ProfileMenuGroup(items: List<ProfileMenuItem>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column {
            items.forEachIndexed { index, item ->
                ProfileMenuRow(label = item.label, onClick = item.onClick)
                if (index != items.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(CardBorder)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileMenuRow(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
            .background(BrandGreen.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            val iconResId = when (label) {
                "Mein Fortschritt" -> R.drawable.ic_profile_progress
                "Abzeichen" -> R.drawable.ic_profile_badges
                "Meine Szenarien" -> R.drawable.ic_profile_scenarios
                "Einstellungen" -> R.drawable.ic_profile_settings
                "Hilfe & Feedback" -> R.drawable.ic_profile_help
                else -> null
            }
            if (iconResId != null) {
                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = label,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(7.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Text(text = label.take(1), color = BrandGreen, style = MaterialTheme.typography.labelLarge)
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            color = BrandBlue,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        Text(text = ">", color = MutedText, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun LogoutRow(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
            .background(TrafficRed.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_profile_logout),
                contentDescription = "Abmelden",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(7.dp),
                contentScale = ContentScale.Fit
            )
        }
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "Abmelden", color = TrafficRed, style = MaterialTheme.typography.titleMedium)
        }
    }
}

val sampleProfile = ProfileUi(
    fullName = "Max Mustermann",
    level = 12,
    currentXp = 2450,
    nextLevelXp = 3000,
    points = 2450,
    streakDays = 7,
    badges = 24
)

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    SsmobileTheme {
        ProfileScreen()
    }
}
