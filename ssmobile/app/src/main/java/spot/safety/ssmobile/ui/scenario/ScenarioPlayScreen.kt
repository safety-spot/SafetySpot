package spot.safety.ssmobile.ui.scenario

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import spot.safety.ssmobile.ui.components.SafetyProgressBar
import spot.safety.ssmobile.ui.theme.AppBackground
import spot.safety.ssmobile.ui.theme.BrandBlue
import spot.safety.ssmobile.ui.theme.BrandGreen
import spot.safety.ssmobile.ui.theme.CardBorder
import spot.safety.ssmobile.ui.theme.ChemieBlueSoft
import spot.safety.ssmobile.ui.theme.ChemieBlueTint
import spot.safety.ssmobile.ui.theme.DangerPink
import spot.safety.ssmobile.ui.theme.MutedText
import spot.safety.ssmobile.ui.theme.PointsYellow
import spot.safety.ssmobile.ui.theme.SafeGreenBg
import spot.safety.ssmobile.ui.theme.SsmobileTheme
import spot.safety.ssmobile.ui.theme.TrafficRed

data class ScenarioPlayUi(
    val currentStep: Int,
    val totalSteps: Int,
    val points: Int,
    val category: String,
    val question: String,
    val instruction: String,
    val context: String
)

@Composable
fun ScenarioPlayScreen(
    modifier: Modifier = Modifier,
    scenario: ScenarioPlayUi = sampleScenarioPlay,
    onBackClick: () -> Unit = {},
    onDangerousClick: () -> Unit = {},
    onSafeClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp)
    ) {
        ScenarioPlayHeader(
            currentStep = scenario.currentStep,
            totalSteps = scenario.totalSteps,
            points = scenario.points,
            onBackClick = onBackClick
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            CategoryTag(label = scenario.category)
            MascotBubble()
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = scenario.question, color = BrandBlue, style = MaterialTheme.typography.displaySmall)
        Text(text = scenario.instruction, color = MutedText, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(18.dp))
        LabIllustration()
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Text(
                text = scenario.context,
                color = BrandBlue,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DecisionButton(
                label = "Gefaehrlich",
                iconText = "X",
                backgroundColor = DangerPink,
                contentColor = TrafficRed,
                onClick = onDangerousClick,
                modifier = Modifier.weight(1f)
            )
            DecisionButton(
                label = "Nicht gefaehrlich",
                iconText = "OK",
                backgroundColor = SafeGreenBg,
                contentColor = BrandGreen,
                onClick = onSafeClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ScenarioPlayHeader(
    currentStep: Int,
    totalSteps: Int,
    points: Int,
    onBackClick: () -> Unit
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
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp)
        ) {
            Text(
                text = "$currentStep / $totalSteps",
                color = BrandBlue,
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.height(6.dp))
            SafetyProgressBar(progress = currentStep.toFloat() / totalSteps.toFloat())
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(99.dp))
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 9.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "* $points", color = PointsYellow, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun CategoryTag(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(ChemieBlueSoft)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(text = label, color = ChemieBlueTint, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun MascotBubble() {
    Box(
        modifier = Modifier
            .size(58.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(BrandGreen.copy(alpha = 0.14f)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Hi", color = BrandGreen, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun LabIllustration() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ChemieBlueSoft),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .border(1.dp, CardBorder, RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Labor", color = ChemieBlueTint, style = MaterialTheme.typography.titleLarge)
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Max arbeitet mit Reagenzglas und Schutzbrille",
                    color = BrandBlue,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }
    }
}

@Composable
private fun DecisionButton(
    label: String,
    iconText: String,
    backgroundColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .height(88.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = iconText, color = contentColor, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = contentColor, style = MaterialTheme.typography.labelLarge)
    }
}

val sampleScenarioPlay = ScenarioPlayUi(
    currentStep = 3,
    totalSteps = 10,
    points = 120,
    category = "Chemieraum",
    question = "Ist das gefaehrlich?",
    instruction = "Lies dir die Situation durch und entscheide.",
    context = "Max fuellt etwas Wasser in ein Reagenzglas, das noch Reste von Schwefelsaeure enthaelt."
)

@Preview(showBackground = true)
@Composable
private fun ScenarioPlayScreenPreview() {
    SsmobileTheme {
        ScenarioPlayScreen()
    }
}
