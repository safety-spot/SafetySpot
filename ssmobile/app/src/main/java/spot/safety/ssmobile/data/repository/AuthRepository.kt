package spot.safety.ssmobile.data.repository

import spot.safety.ssmobile.data.TokenStore
import spot.safety.ssmobile.data.model.LoginRequest
import spot.safety.ssmobile.data.model.LogoutRequest
import spot.safety.ssmobile.data.model.RegisterRequest
import spot.safety.ssmobile.data.network.SafetySpotApi

class AuthRepository(private val api: SafetySpotApi, private val tokenStore: TokenStore) {

    suspend fun loginAndStore(username: String, password: String): Result<String> = runCatching {
        val response = api.login(LoginRequest(username, password))
        if (!response.isSuccessful) error("Login fehlgeschlagen: ${response.code()}")
        val raw = response.body() ?: error("Kein Token zurückgegeben")
        val token = raw.trim('"')
        tokenStore.token = token
        tokenStore.username = username
        username
    }

    suspend fun register(username: String, password: String, schoolName: String): Result<Unit> = runCatching {
        val response = api.register(RegisterRequest(username, password, schoolName, "STUDENT"))
        if (!response.isSuccessful) error("Registrierung fehlgeschlagen: ${response.code()}")
    }

    suspend fun logout(): Result<Unit> = runCatching {
        try {
            tokenStore.token?.let { token ->
                api.logout(LogoutRequest(token))
            }
        } finally {
            tokenStore.clear()
        }
    }

    val isLoggedIn: Boolean get() = tokenStore.token != null
}
