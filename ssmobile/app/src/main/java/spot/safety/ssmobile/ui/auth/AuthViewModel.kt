package spot.safety.ssmobile.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import spot.safety.ssmobile.data.repository.AuthRepository

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false
)

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(username: String, password: String) {
        _uiState.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            authRepository.loginAndStore(username, password)
                .onSuccess { _uiState.value = AuthUiState(isAuthenticated = true) }
                .onFailure { _uiState.value = AuthUiState(error = it.message ?: "Unbekannter Fehler") }
        }
    }

    fun register(username: String, password: String, schoolName: String) {
        _uiState.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            authRepository.register(username, password, schoolName)
                .onSuccess {
                    authRepository.loginAndStore(username, password)
                        .onSuccess { _uiState.value = AuthUiState(isAuthenticated = true) }
                        .onFailure { _uiState.value = AuthUiState(error = "Registriert, aber Login fehlgeschlagen.") }
                }
                .onFailure { _uiState.value = AuthUiState(error = it.message ?: "Registrierung fehlgeschlagen") }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearAuthentication() {
        _uiState.value = AuthUiState()
    }

    companion object {
        fun factory(authRepository: AuthRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    AuthViewModel(authRepository) as T
            }
    }
}
