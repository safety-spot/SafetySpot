package spot.safety.ssmobile.data.repository

import spot.safety.ssmobile.data.model.LeaderboardRequest
import spot.safety.ssmobile.data.model.LeaderboardResponse
import spot.safety.ssmobile.data.model.SchoolClassRef
import spot.safety.ssmobile.data.network.SafetySpotApi

class LeaderboardRepository(private val api: SafetySpotApi) {

    suspend fun getLeaderboard(classId: Long): Result<LeaderboardResponse> = runCatching {
        api.getLeaderboard(LeaderboardRequest(SchoolClassRef(classId)))
    }
}
