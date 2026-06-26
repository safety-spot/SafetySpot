package spot.safety.ssmobile.ui.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import spot.safety.ssmobile.data.TokenStore
import spot.safety.ssmobile.data.repository.LeaderboardRepository

data class RankingUiState(
    val isLoading: Boolean = true,
    val entries: List<LeaderboardEntryUi> = emptyList(),
    val error: String? = null
)

class RankingViewModel(
    private val leaderboardRepository: LeaderboardRepository,
    private val tokenStore: TokenStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(RankingUiState())
    val uiState: StateFlow<RankingUiState> = _uiState

    init {
        load()
    }

    fun load() {
        val classId = tokenStore.classId ?: run {
            _uiState.value = RankingUiState(isLoading = false, entries = emptyList())
            return
        }
        viewModelScope.launch {
            leaderboardRepository.getLeaderboard(classId)
                .onSuccess { leaderboard ->
                    val myUsername = tokenStore.username
                    val entries = leaderboard.entries
                        .sortedBy { it.rank }
                        .map { entry ->
                            val name = entry.user?.username ?: "Unbekannt"
                            LeaderboardEntryUi(
                                rank = entry.rank,
                                name = name,
                                score = entry.totalPoints,
                                avatarText = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                isCurrentUser = name == myUsername
                            )
                        }
                    _uiState.value = RankingUiState(isLoading = false, entries = entries)
                }
                .onFailure {
                    _uiState.value = RankingUiState(isLoading = false, error = it.message)
                }
        }
    }

    companion object {
        fun factory(leaderboardRepository: LeaderboardRepository, tokenStore: TokenStore): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    RankingViewModel(leaderboardRepository, tokenStore) as T
            }
    }
}
