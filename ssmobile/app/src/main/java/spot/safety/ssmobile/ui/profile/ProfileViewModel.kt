package spot.safety.ssmobile.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import spot.safety.ssmobile.data.TokenStore
import spot.safety.ssmobile.data.repository.ProgressRepository

data class ProfileViewState(
    val isLoading: Boolean = true,
    val profile: ProfileUi? = null,
    val error: String? = null
)

class ProfileViewModel(
    private val progressRepository: ProgressRepository,
    private val tokenStore: TokenStore
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileViewState())
    val state: StateFlow<ProfileViewState> = _state

    init {
        load()
    }

    fun load() {
        _state.value = ProfileViewState(isLoading = true)
        viewModelScope.launch {
            progressRepository.getSummary()
                .onSuccess { summary ->
                    val points = (summary.correctCount * 40).toInt()
                    val level = 1 + (points / 500)
                    _state.value = ProfileViewState(
                        isLoading = false,
                        profile = ProfileUi(
                            fullName = tokenStore.username ?: "Benutzer",
                            level = level,
                            currentXp = points,
                            nextLevelXp = (level + 1) * 500,
                            points = points,
                            streakDays = 0,
                            badges = summary.totalTagged.toInt() / 5
                        )
                    )
                }
                .onFailure {
                    _state.value = ProfileViewState(isLoading = false, error = it.message)
                }
        }
    }

    companion object {
        fun factory(progressRepository: ProgressRepository, tokenStore: TokenStore): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    ProfileViewModel(progressRepository, tokenStore) as T
            }
    }
}
