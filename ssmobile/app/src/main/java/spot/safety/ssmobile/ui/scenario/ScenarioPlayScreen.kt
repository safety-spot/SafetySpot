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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import spot.safety.ssmobile.ui.theme.SportGreen
import spot.safety.ssmobile.ui.theme.SportGreenSoft
import spot.safety.ssmobile.ui.theme.SsmobileTheme
import spot.safety.ssmobile.ui.theme.TechnikPurple
import spot.safety.ssmobile.ui.theme.TechnikPurpleSoft
import spot.safety.ssmobile.ui.theme.TrafficRed
import spot.safety.ssmobile.ui.theme.WerkraumOrange
import spot.safety.ssmobile.ui.theme.WerkraumOrangeSoft

data class ScenarioPlayUi(
    val category: String,
    val question: String,
    val instruction: String,
    val context: String,
    val illustrationTitle: String,
    val illustrationDescription: String,
    val illustrationColor: Color,
    val illustrationBackground: Color,
    val isDangerous: Boolean,
    val feedbackCorrect: String,
    val feedbackWrong: String,
    val points: Int
)

@Composable
fun ScenarioPlayScreen(
    modifier: Modifier = Modifier,
    scenarioId: Int = 1,
    scenarios: List<ScenarioPlayUi> = tasksForScenario(scenarioId),
    onScenarioCompleted: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    var currentIndex by rememberSaveable { mutableIntStateOf(0) }
    var selectedDangerous by rememberSaveable { mutableStateOf<Boolean?>(null) }
    var earnedPoints by rememberSaveable { mutableIntStateOf(0) }
    var isComplete by rememberSaveable { mutableStateOf(false) }

    val currentScenario = scenarios[currentIndex]
    val selectedAnswer = selectedDangerous
    val wasCorrect = selectedAnswer?.let { it == currentScenario.isDangerous }

    if (isComplete) {
        ScenarioResultScreen(
            earnedPoints = earnedPoints,
            maxPoints = scenarios.sumOf { it.points },
            onBackClick = onBackClick,
            onRestartClick = {
                currentIndex = 0
                selectedDangerous = null
                earnedPoints = 0
                isComplete = false
            },
            modifier = modifier
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp)
    ) {
        ScenarioPlayHeader(
            currentStep = currentIndex + 1,
            totalSteps = scenarios.size,
            points = earnedPoints,
            onBackClick = onBackClick
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            CategoryTag(label = currentScenario.category)
            MascotBubble()
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = currentScenario.question, color = BrandBlue, style = MaterialTheme.typography.displaySmall)
        Text(text = currentScenario.instruction, color = MutedText, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(18.dp))
        ScenarioIllustration(
            title = currentScenario.illustrationTitle,
            description = currentScenario.illustrationDescription,
            accentColor = currentScenario.illustrationColor,
            backgroundColor = currentScenario.illustrationBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Text(
                text = currentScenario.context,
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
                selected = selectedAnswer == true,
                enabled = selectedAnswer == null,
                onClick = { selectedDangerous = true },
                modifier = Modifier.weight(1f)
            )
            DecisionButton(
                label = "Nicht gefaehrlich",
                iconText = "OK",
                backgroundColor = SafeGreenBg,
                contentColor = BrandGreen,
                selected = selectedAnswer == false,
                enabled = selectedAnswer == null,
                onClick = { selectedDangerous = false },
                modifier = Modifier.weight(1f)
            )
        }
        if (wasCorrect != null) {
            Spacer(modifier = Modifier.height(16.dp))
            FeedbackCard(
                correct = wasCorrect,
                text = if (wasCorrect) currentScenario.feedbackCorrect else currentScenario.feedbackWrong,
                points = if (wasCorrect) currentScenario.points else 0
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = {
                    if (wasCorrect) {
                        earnedPoints += currentScenario.points
                    }
                    if (currentIndex == scenarios.lastIndex) {
                        onScenarioCompleted()
                        isComplete = true
                    } else {
                        currentIndex += 1
                        selectedDangerous = null
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(99.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
            ) {
                Text(
                    text = if (currentIndex == scenarios.lastIndex) "Ergebnis anzeigen" else "Weiter",
                    style = MaterialTheme.typography.labelLarge
                )
            }
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
private fun ScenarioIllustration(
    title: String,
    description: String,
    accentColor: Color,
    backgroundColor: Color
) {
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
                .background(backgroundColor),
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
                    Text(text = title, color = accentColor, style = MaterialTheme.typography.titleLarge)
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = description,
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
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .height(88.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(
                width = if (selected) 2.dp else 0.dp,
                color = if (selected) contentColor else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = iconText, color = contentColor, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = contentColor, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun FeedbackCard(
    correct: Boolean,
    text: String,
    points: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (correct) SafeGreenBg.copy(alpha = 0.65f) else DangerPink.copy(alpha = 0.65f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (correct) "Richtig" else "Fast",
                color = if (correct) BrandGreen else TrafficRed,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = text, color = BrandBlue, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "+$points Punkte",
                color = if (correct) BrandGreen else MutedText,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun ScenarioResultScreen(
    earnedPoints: Int,
    maxPoints: Int,
    onBackClick: () -> Unit,
    onRestartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(horizontal = 22.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Szenario geschafft", color = BrandBlue, style = MaterialTheme.typography.displaySmall)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "$earnedPoints von $maxPoints Punkten",
            color = PointsYellow,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(18.dp))
        SafetyProgressBar(
            progress = earnedPoints.toFloat() / maxPoints.toFloat(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(28.dp))
        Button(
            onClick = onRestartClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(99.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
        ) {
            Text(text = "Nochmal spielen", style = MaterialTheme.typography.labelLarge)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Zurueck",
            color = BrandBlue,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .clip(RoundedCornerShape(99.dp))
                .clickable(onClick = onBackClick)
                .padding(horizontal = 18.dp, vertical = 10.dp)
        )
    }
}

fun tasksForScenario(scenarioId: Int): List<ScenarioPlayUi> = when (scenarioId) {
    2 -> listOf(
        ScenarioPlayUi(
            category = "Werkraum",
            question = "Ist das gefaehrlich?",
            instruction = "Lies dir die Situation durch und entscheide.",
            context = "Noah benutzt eine Saege, waehrend das Werkstueck locker auf dem Tisch liegt.",
            illustrationTitle = "Werkbank",
            illustrationDescription = "Noah steht an einer Werkbank mit Saege und Holzbrett",
            illustrationColor = WerkraumOrange,
            illustrationBackground = WerkraumOrangeSoft,
            isDangerous = true,
            feedbackCorrect = "Richtig. Werkstuecke muessen sicher eingespannt werden.",
            feedbackWrong = "Das ist gefaehrlich: Ein loses Werkstueck kann verrutschen.",
            points = 40
        ),
        ScenarioPlayUi(
            category = "Werkraum",
            question = "Ist das gefaehrlich?",
            instruction = "Lies dir die Situation durch und entscheide.",
            context = "Mia traegt eine Schutzbrille, bevor sie Holz schleift.",
            illustrationTitle = "Schutzbrille",
            illustrationDescription = "Mia bereitet sich mit Schutzbrille auf Schleifarbeiten vor",
            illustrationColor = WerkraumOrange,
            illustrationBackground = WerkraumOrangeSoft,
            isDangerous = false,
            feedbackCorrect = "Genau. Die Schutzbrille schuetzt vor Staub und Splittern.",
            feedbackWrong = "Das ist eine sichere Schutzmassnahme.",
            points = 40
        )
    )

    3 -> listOf(
        ScenarioPlayUi(
            category = "Sportunterricht",
            question = "Ist das gefaehrlich?",
            instruction = "Lies dir die Situation durch und entscheide.",
            context = "Die Klasse waermt sich vor dem Sprinttraining gemeinsam auf.",
            illustrationTitle = "Sporthalle",
            illustrationDescription = "Die Gruppe macht Aufwaermuebungen in der Sporthalle",
            illustrationColor = SportGreen,
            illustrationBackground = SportGreenSoft,
            isDangerous = false,
            feedbackCorrect = "Richtig. Aufwaermen senkt das Verletzungsrisiko.",
            feedbackWrong = "Das ist nicht gefaehrlich, sondern eine gute Vorbereitung.",
            points = 40
        ),
        ScenarioPlayUi(
            category = "Sportunterricht",
            question = "Ist das gefaehrlich?",
            instruction = "Lies dir die Situation durch und entscheide.",
            context = "Jonas springt auf eine nasse Turnmatte.",
            illustrationTitle = "Turnmatte",
            illustrationDescription = "Eine nasse Matte liegt in der Sporthalle",
            illustrationColor = SportGreen,
            illustrationBackground = SportGreenSoft,
            isDangerous = true,
            feedbackCorrect = "Richtig. Nasse Matten koennen rutschen.",
            feedbackWrong = "Das ist gefaehrlich, weil die Matte keinen sicheren Halt gibt.",
            points = 40
        )
    )

    4 -> listOf(
        ScenarioPlayUi(
            category = "Strassenverkehr",
            question = "Ist das gefaehrlich?",
            instruction = "Lies dir die Situation durch und entscheide.",
            context = "Max schaut nach links und rechts, bevor er den Zebrastreifen betritt.",
            illustrationTitle = "Zebrastreifen",
            illustrationDescription = "Max wartet aufmerksam am Strassenrand",
            illustrationColor = TrafficRed,
            illustrationBackground = Color(0xFFFFEBEE),
            isDangerous = false,
            feedbackCorrect = "Genau. Auch am Zebrastreifen muss man aufmerksam bleiben.",
            feedbackWrong = "Das ist nicht gefaehrlich, sondern umsichtig.",
            points = 40
        ),
        ScenarioPlayUi(
            category = "Strassenverkehr",
            question = "Ist das gefaehrlich?",
            instruction = "Lies dir die Situation durch und entscheide.",
            context = "Mia laeuft zwischen parkenden Autos auf die Strasse.",
            illustrationTitle = "Strasse",
            illustrationDescription = "Parkende Autos verdecken die Sicht auf die Fahrbahn",
            illustrationColor = TrafficRed,
            illustrationBackground = Color(0xFFFFEBEE),
            isDangerous = true,
            feedbackCorrect = "Richtig. Andere Verkehrsteilnehmer sehen sie dort schlecht.",
            feedbackWrong = "Das ist gefaehrlich, weil sie spaet gesehen wird.",
            points = 40
        )
    )

    5 -> listOf(
        ScenarioPlayUi(
            category = "Technikraum",
            question = "Ist das gefaehrlich?",
            instruction = "Lies dir die Situation durch und entscheide.",
            context = "Ein Kabel hat eine beschaedigte Isolierung und wird trotzdem benutzt.",
            illustrationTitle = "Kabel",
            illustrationDescription = "Ein defektes Kabel liegt neben einem elektrischen Geraet",
            illustrationColor = TechnikPurple,
            illustrationBackground = TechnikPurpleSoft,
            isDangerous = true,
            feedbackCorrect = "Richtig. Beschaedigte Kabel duerfen nicht verwendet werden.",
            feedbackWrong = "Das ist gefaehrlich: Stromschlag- und Brandgefahr.",
            points = 40
        ),
        ScenarioPlayUi(
            category = "Technikraum",
            question = "Ist das gefaehrlich?",
            instruction = "Lies dir die Situation durch und entscheide.",
            context = "Paul schaltet das Geraet aus, bevor er den Stecker zieht.",
            illustrationTitle = "Stecker",
            illustrationDescription = "Paul trennt ein ausgeschaltetes Geraet sicher vom Strom",
            illustrationColor = TechnikPurple,
            illustrationBackground = TechnikPurpleSoft,
            isDangerous = false,
            feedbackCorrect = "Genau. Erst ausschalten, dann sicher trennen.",
            feedbackWrong = "Das ist nicht gefaehrlich, sondern richtiges Vorgehen.",
            points = 40
        )
    )

    else -> listOf(
        ScenarioPlayUi(
            category = "Chemieraum",
            question = "Ist das gefaehrlich?",
            instruction = "Lies dir die Situation durch und entscheide.",
            context = "Max fuellt etwas Wasser in ein Reagenzglas, das noch Reste von Schwefelsaeure enthaelt.",
            illustrationTitle = "Labor",
            illustrationDescription = "Max arbeitet mit Reagenzglas und Schutzbrille",
            illustrationColor = ChemieBlueTint,
            illustrationBackground = ChemieBlueSoft,
            isDangerous = true,
            feedbackCorrect = "Richtig. Saeurereste koennen mit Wasser reagieren und spritzen.",
            feedbackWrong = "Das ist gefaehrlich: Saeurereste koennen reagieren und Verletzungen verursachen.",
            points = 40
        ),
        ScenarioPlayUi(
            category = "Chemieraum",
            question = "Ist das gefaehrlich?",
            instruction = "Lies dir die Situation durch und entscheide.",
            context = "Mia setzt ihre Schutzbrille auf, bevor sie mit den Chemikalien arbeitet.",
            illustrationTitle = "Schutz",
            illustrationDescription = "Mia setzt im Chemieraum ihre Schutzbrille auf",
            illustrationColor = ChemieBlueTint,
            illustrationBackground = ChemieBlueSoft,
            isDangerous = false,
            feedbackCorrect = "Genau. Die Schutzbrille reduziert das Risiko fuer Augenverletzungen.",
            feedbackWrong = "Das ist nicht gefaehrlich, sondern eine wichtige Schutzmassnahme.",
            points = 40
        ),
        ScenarioPlayUi(
            category = "Chemieraum",
            question = "Ist das gefaehrlich?",
            instruction = "Lies dir die Situation durch und entscheide.",
            context = "Jonas riecht direkt an einer unbekannten Fluessigkeit im Becherglas.",
            illustrationTitle = "Becherglas",
            illustrationDescription = "Ein Becherglas mit unbekannter Fluessigkeit steht auf dem Labortisch",
            illustrationColor = ChemieBlueTint,
            illustrationBackground = ChemieBlueSoft,
            isDangerous = true,
            feedbackCorrect = "Richtig. Unbekannte Stoffe duerfen nicht direkt eingeatmet werden.",
            feedbackWrong = "Das ist gefaehrlich: Daempfe koennen reizend oder giftig sein.",
            points = 40
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun ScenarioPlayScreenPreview() {
    SsmobileTheme {
        ScenarioPlayScreen()
    }
}
