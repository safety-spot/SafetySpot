package spot.safety.ssmobile.ui.scenarios

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
import spot.safety.ssmobile.ui.theme.DifficultyMedium
import spot.safety.ssmobile.ui.theme.SportGreen
import spot.safety.ssmobile.ui.theme.SportGreenSoft
import spot.safety.ssmobile.ui.theme.TechnikPurple
import spot.safety.ssmobile.ui.theme.TechnikPurpleSoft
import spot.safety.ssmobile.ui.theme.WerkraumOrange
import spot.safety.ssmobile.ui.theme.WerkraumOrangeSoft

data class ScenariosUiState(
    val isLoading: Boolean = true,
    val scenarios: List<ScenarioUi> = emptyList(),
    val error: String? = null
)

class ScenariosViewModel(private val imageRepository: ImageRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ScenariosUiState())
    val uiState: StateFlow<ScenariosUiState> = _uiState

    init {
        load()
    }

    fun load() {
        _uiState.value = ScenariosUiState(isLoading = true)
        viewModelScope.launch {
            imageRepository.getImages()
                .onSuccess { images ->
                    val grouped = images.groupBy { it.category ?: "Allgemein" }
                    val scenarios = grouped.entries.mapIndexed { index, (category, imgs) ->
                        val (accent, bg, icon) = categoryStyle(category)
                        ScenarioUi(
                            id = index + 1,
                            title = category,
                            subtitle = "${imgs.size} Bilder",
                            difficultyLabel = "Mittel",
                            difficultyColor = DifficultyMedium,
                            taskCount = imgs.size,
                            isNew = true,
                            accentColor = accent,
                            backgroundColor = bg,
                            iconText = icon,
                            category = category
                        )
                    }
                    _uiState.value = ScenariosUiState(isLoading = false, scenarios = scenarios)
                }
                .onFailure {
                    _uiState.value = ScenariosUiState(isLoading = false, error = it.message)
                }
        }
    }

    private fun categoryStyle(category: String): Triple<Color, Color, String> = when {
        category.contains("Chemie", ignoreCase = true) -> Triple(ChemieBlueTint, ChemieBlueSoft, "C")
        category.contains("Werk", ignoreCase = true) -> Triple(WerkraumOrange, WerkraumOrangeSoft, "W")
        category.contains("Sport", ignoreCase = true) -> Triple(SportGreen, SportGreenSoft, "S")
        category.contains("Technik", ignoreCase = true) -> Triple(TechnikPurple, TechnikPurpleSoft, "T")
        else -> Triple(ChemieBlueTint, ChemieBlueSoft, category.firstOrNull()?.uppercaseChar()?.toString() ?: "?")
    }

    companion object {
        fun factory(imageRepository: ImageRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    ScenariosViewModel(imageRepository) as T
            }
    }
}
