package spot.safety.ssmobile.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import spot.safety.ssmobile.data.TokenStore
import spot.safety.ssmobile.data.repository.ImageRepository
import spot.safety.ssmobile.data.repository.ProgressRepository

data class HomeUiState(
    val isLoading: Boolean = true,
    val displayName: String = "",
    val points: Int = 0,
    val completedCount: Int = 0,
    val categories: List<String> = emptyList(),
    val categoryCounts: Map<String, Int> = emptyMap(),
    val error: String? = null
)

class HomeViewModel(
    private val imageRepository: ImageRepository,
    private val progressRepository: ProgressRepository,
    private val tokenStore: TokenStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        load()
    }

    fun load() {
        _uiState.value = HomeUiState(isLoading = true)
        viewModelScope.launch {
            val name = tokenStore.username ?: "Benutzer"
            var points = 0
            var completedCount = 0
            var categories = emptyList<String>()
            var categoryCounts = emptyMap<String, Int>()

            progressRepository.getSummary().onSuccess {
                points = (it.correctCount * 40).toInt()
                completedCount = it.totalTagged.toInt()
            }
            imageRepository.getImages().onSuccess { images ->
                categoryCounts = images
                    .map { it.category ?: "Allgemein" }
                    .groupingBy { it }
                    .eachCount()
                categories = categoryCounts.keys.toList()
            }

            _uiState.value = HomeUiState(
                isLoading = false,
                displayName = name,
                points = points,
                completedCount = completedCount,
                categories = categories,
                categoryCounts = categoryCounts
            )
        }
    }

    companion object {
        fun factory(
            imageRepository: ImageRepository,
            progressRepository: ProgressRepository,
            tokenStore: TokenStore
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    HomeViewModel(imageRepository, progressRepository, tokenStore) as T
            }
    }
}
