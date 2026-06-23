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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import spot.safety.ssmobile.ui.theme.AppBackground
import spot.safety.ssmobile.ui.theme.BrandBlue
import spot.safety.ssmobile.ui.theme.BrandGreen
import spot.safety.ssmobile.ui.theme.CardBorder
import spot.safety.ssmobile.ui.theme.MutedText
import spot.safety.ssmobile.ui.theme.SafeGreenBg
import spot.safety.ssmobile.ui.theme.SsmobileTheme

private data class FaqItem(
    val question: String,
    val answer: String
)

@Composable
fun HelpFeedbackScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    var expandedFaqIndex by rememberSaveable { mutableIntStateOf(0) }
    var feedbackText by rememberSaveable { mutableStateOf("") }
    var feedbackSent by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp)
    ) {
        HelpHeader(onBackClick = onBackClick)
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Schnelle Antworten und ein Mock-Feedbackformular fuer spaetere Backend-Anbindung.",
            color = MutedText,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(18.dp))
        Text(text = "FAQ", color = BrandBlue, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            faqItems.forEachIndexed { index, item ->
                FaqCard(
                    item = item,
                    expanded = expandedFaqIndex == index,
                    onClick = { expandedFaqIndex = index }
                )
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        FeedbackCard(
            value = feedbackText,
            sent = feedbackSent,
            onValueChange = {
                feedbackText = it
                feedbackSent = false
            },
            onSendClick = {
                if (feedbackText.isNotBlank()) {
                    feedbackSent = true
                    feedbackText = ""
                }
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun HelpHeader(onBackClick: () -> Unit) {
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
            text = "Hilfe & Feedback",
            color = BrandBlue,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 14.dp)
        )
    }
}

@Composable
private fun FaqCard(
    item: FaqItem,
    expanded: Boolean,
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
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.question,
                    color = BrandBlue,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (expanded) "-" else "+",
                    color = BrandGreen,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = item.answer, color = MutedText, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun FeedbackCard(
    value: String,
    sent: Boolean,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Feedback senden", color = BrandBlue, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(118.dp),
                placeholder = {
                    Text(text = "Was soll verbessert werden?", color = MutedText)
                },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandGreen,
                    unfocusedBorderColor = CardBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
            if (sent) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SafeGreenBg)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Danke, dein Feedback ist lokal vorgemerkt.",
                        color = BrandGreen,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onSendClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(99.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
            ) {
                Text(text = "Absenden", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

private val faqItems = listOf(
    FaqItem(
        question = "Wie sammle ich Punkte?",
        answer = "Du bekommst Punkte, wenn du Situationen im Szenario richtig einschaetzt."
    ),
    FaqItem(
        question = "Warum ist mein Fortschritt lokal?",
        answer = "Das Frontend nutzt aktuell Mock-State. Die echte Speicherung kommt spaeter ueber das Backend."
    ),
    FaqItem(
        question = "Kann ich Szenarien wiederholen?",
        answer = "Ja. Wiederholen ist moeglich, der Mock-Score wird aber nur beim ersten Abschluss gutgeschrieben."
    )
)

@Preview(showBackground = true)
@Composable
private fun HelpFeedbackScreenPreview() {
    SsmobileTheme {
        HelpFeedbackScreen()
    }
}
