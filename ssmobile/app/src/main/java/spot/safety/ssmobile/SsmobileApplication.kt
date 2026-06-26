package spot.safety.ssmobile

import android.app.Application
import spot.safety.ssmobile.data.TokenStore
import spot.safety.ssmobile.data.network.ApiClient
import spot.safety.ssmobile.data.repository.AuthRepository
import spot.safety.ssmobile.data.repository.ImageRepository
import spot.safety.ssmobile.data.repository.LeaderboardRepository
import spot.safety.ssmobile.data.repository.ProgressRepository

class SsmobileApplication : Application() {
    lateinit var tokenStore: TokenStore
    lateinit var authRepository: AuthRepository
    lateinit var imageRepository: ImageRepository
    lateinit var leaderboardRepository: LeaderboardRepository
    lateinit var progressRepository: ProgressRepository

    override fun onCreate() {
        super.onCreate()
        instance = this
        tokenStore = TokenStore(this)
        val api = ApiClient.create(tokenStore)
        authRepository = AuthRepository(api, tokenStore)
        imageRepository = ImageRepository(api, tokenStore)
        leaderboardRepository = LeaderboardRepository(api)
        progressRepository = ProgressRepository(api)
    }

    companion object {
        lateinit var instance: SsmobileApplication
            private set
    }
}
