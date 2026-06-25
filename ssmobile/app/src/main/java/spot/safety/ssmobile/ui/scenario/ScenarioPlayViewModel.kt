package spot.safety.ssmobile.ui.scenario

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import spot.safety.ssmobile.data.TokenStore
import spot.safety.ssmobile.data.model.TagValue
import spot.safety.ssmobile.data.network.ApiClient
import spot.safety.ssmobile.data.repository.ImageRepository
import spot.safety.ssmobile.data.repository.ProgressRepository
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
    val completedBeforeCount: Int = 0,
    val totalTaskCount: Int = 0,
    val error: String? = null
)

data class TagResult(val correct: Boolean, val feedback: String, val points: Int)

class ScenarioPlayViewModel(
    private val imageRepository: ImageRepository,
    private val progressRepository: ProgressRepository,
    private val tokenStore: TokenStore,
    private val category: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScenarioPlayUiState())
    val uiState: StateFlow<ScenarioPlayUiState> = _uiState

    init {
        tokenStore.lastScenarioCategory = category
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val taggedImageIds = tokenStore.taggedImageIds() +
                (progressRepository.getHistory()
                    .getOrNull()
                    ?.map { it.imageId }
                    ?.toSet()
                    ?: emptySet())
            imageRepository.getImages(category)
                .onSuccess { images ->
                    val remainingImages = images.filter { it.id !in taggedImageIds }
                    val tasks = remainingImages
                        .map { img ->
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
                            imageUrl = img.imageUrl?.toBackendImageUrl(),
                            isDangerous = false,
                            feedbackCorrect = "Richtig!",
                            feedbackWrong = "Leider falsch.",
                            points = 40,
                            imageRepository,
                        )
                    }
                    _uiState.value = ScenarioPlayUiState(
                        isLoading = false,
                        tasks = tasks,
                        completedBeforeCount = images.size - remainingImages.size,
                        totalTaskCount = images.size
                    )
                }
                .onFailure {
                    _uiState.value = ScenarioPlayUiState(isLoading = false, error = it.message)
                }
        }
    }

    suspend fun submitTag(imageId: Long, isDangerous: Boolean): TagResult {
        val tag = if (isDangerous) TagValue.DANGEROUS else TagValue.SAFE
        tokenStore.lastScenarioCategory = category
        return imageRepository.submitTag(imageId, tag)
            .map { response ->
                tokenStore.addTaggedImageId(imageId)
                TagResult(
                    correct = response.correct,
                    feedback = response.feedback ?: if (response.correct) "Richtig!" else "Falsch!",
                    points = if (response.correct) 40 else 0
                )
            }
            .getOrElse { error ->
                when ((error as? HttpException)?.code()) {
                    409 -> {
                        tokenStore.addTaggedImageId(imageId)
                        TagResult(
                            correct = false,
                            feedback = "Diese Aufgabe wurde bereits beantwortet.",
                            points = 0
                        )
                    }
                    401 -> TagResult(correct = false, feedback = "Nicht angemeldet. Bitte neu einloggen.", points = 0)
                    403 -> TagResult(correct = false, feedback = "Dein Account darf diese Aufgabe nicht beantworten.", points = 0)
                    404 -> TagResult(correct = false, feedback = "Diese Aufgabe wurde im Backend nicht gefunden.", points = 0)
                    500 -> TagResult(correct = false, feedback = "Backend-Fehler beim Speichern der Antwort.", points = 0)
                    null -> TagResult(
                        correct = false,
                        feedback = error.localizedMessage ?: "Netzwerkfehler beim Senden.",
                        points = 0
                    )
                    else -> TagResult(
                        correct = false,
                        feedback = "Fehler beim Senden: HTTP ${(error as HttpException).code()}",
                        points = 0
                    )
                }
            }
    }

    private fun categoryColors(category: String): Pair<Color, Color> = when {
        category.contains("Chemie", ignoreCase = true) -> ChemieBlueTint to ChemieBlueSoft
        category.contains("Werk", ignoreCase = true) -> WerkraumOrange to WerkraumOrangeSoft
        category.contains("Sport", ignoreCase = true) -> SportGreen to SportGreenSoft
        category.contains("Technik", ignoreCase = true) -> TechnikPurple to TechnikPurpleSoft
        else -> ChemieBlueTint to ChemieBlueSoft
    }

    private fun String.toBackendImageUrl(): String {
        if (startsWith("http://") || startsWith("https://")) return this
        return ApiClient.BASE_URL.trimEnd('/') + "/" + trimStart('/')
    }

    companion object {
        fun factory(
            imageRepository: ImageRepository,
            progressRepository: ProgressRepository,
            tokenStore: TokenStore,
            category: String
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    ScenarioPlayViewModel(imageRepository, progressRepository, tokenStore, category) as T
            }
    }
}
