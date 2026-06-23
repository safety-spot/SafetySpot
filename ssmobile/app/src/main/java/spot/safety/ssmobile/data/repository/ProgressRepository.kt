package spot.safety.ssmobile.data.repository

import spot.safety.ssmobile.data.model.ProgressEntryResponse
import spot.safety.ssmobile.data.model.ProgressSummaryResponse
import spot.safety.ssmobile.data.network.SafetySpotApi

class ProgressRepository(private val api: SafetySpotApi) {

    suspend fun getSummary(): Result<ProgressSummaryResponse> = runCatching {
        api.getProgressSummary()
    }

    suspend fun getHistory(): Result<List<ProgressEntryResponse>> = runCatching {
        api.getProgressHistory()
    }
}
