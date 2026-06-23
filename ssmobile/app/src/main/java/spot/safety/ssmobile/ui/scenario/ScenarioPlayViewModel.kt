package spot.safety.ssmobile.ui.scenario

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import spot.safety.ssmobile.data.repository.ImageRepository
import spot.safety.ssmobile.ui.theme.ChemieBlueSoft
import spot.safety.ssmobile.ui.theme.ChemieBlueTint
import spot.safety.ssmobile.ui.theme.SportGreen
import spot.safety.ssmobile.ui.theme.SportGreenSoft
import spot.safety.ssmobile.ui.theme.TechnikPurple
import spot.safety.ssmobile.ui.theme.TechnikPurpleSoft
import spot.safety.ssmobile.ui.theme.WerkraumOrange
import spot.safety.ssmobile.ui.theme.WerkraumOrangeSoft

data class ScenarioPlayUiState(
    val isLoading: Boolean = true,
    val tasks: List<ScenarioPlayUi> = emptyList(),
    val error: String? = null
)

data class TagResult(val correct: Boolean, val feedback: String, val points: Int)

class ScenarioPlayViewModel(
    private val imageRepository: ImageRepository,
    private val category: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScenarioPlayUiState())
    val uiState: StateFlow<ScenarioPlayUiState> = _uiState

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            imageRepository.getImages(category)
                .onSuccess { images ->
                    val tasks = images.map { img ->
                        val (accent, bg) = categoryColors(img.category ?: "")
                        ScenarioPlayUi(
                            imageId = img.id,
                            category = img.category ?: "Allgemein",
                            question = "Ist diese Situation sicher oder gefährlich?",
                            instruction = "Beurteile die folgende Situation.",
                            context = img.description ?: img.title,
                            illustrationTitle = img.title,
                            illustrationDescription = img.description ?: "",
                            illustrationColor = accent,
                            illustrationBackground = bg,
                            imageUrl = img.imageUrl,
                            isDangerous = false,
                            feedbackCorrect = "Richtig!",
                            feedbackWrong = "Leider falsch.",
                            points = 40
                        )
                    }
                    _uiState.value = ScenarioPlayUiState(isLoading = false, tasks = tasks)
                }
                .onFailure {
                    _uiState.value = ScenarioPlayUiState(isLoading = false, error = it.message)
                }
        }
    }

    suspend fun submitTag(imageId: Long, isDangerous: Boolean): TagResult {
        val tag = if (isDangerous) "DANGEROUS" else "SAFE"
        return imageRepository.submitTag(imageId, tag)
            .map { response ->
                TagResult(
                    correct = response.correct,
                    feedback = response.feedback ?: if (response.correct) "Richtig!" else "Falsch!",
                    points = if (response.correct) 40 else 0
                )
            }
            .getOrElse { TagResult(correct = false, feedback = "Fehler beim Senden.", points = 0) }
    }

    private fun categoryColors(category: String): Pair<Color, Color> = when {
        category.contains("Chemie", ignoreCase = true) -> ChemieBlueTint to ChemieBlueSoft
        category.contains("Werk", ignoreCase = true) -> WerkraumOrange to WerkraumOrangeSoft
        category.contains("Sport", ignoreCase = true) -> SportGreen to SportGreenSoft
        category.contains("Technik", ignoreCase = true) -> TechnikPurple to TechnikPurpleSoft
        else -> ChemieBlueTint to ChemieBlueSoft
    }

    companion object {
        fun factory(imageRepository: ImageRepository, category: String): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    ScenarioPlayViewModel(imageRepository, category) as T
            }
    }
}
