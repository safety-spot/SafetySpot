package spot.safety.ssmobile.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import spot.safety.ssmobile.data.model.ImageResponse
import spot.safety.ssmobile.data.model.LeaderboardRequest
import spot.safety.ssmobile.data.model.LeaderboardResponse
import spot.safety.ssmobile.data.model.LoginRequest
import spot.safety.ssmobile.data.model.LogoutRequest
import spot.safety.ssmobile.data.model.ProgressEntryResponse
import spot.safety.ssmobile.data.model.ProgressSummaryResponse
import spot.safety.ssmobile.data.model.RegisterRequest
import spot.safety.ssmobile.data.model.SubmitTagRequest
import spot.safety.ssmobile.data.model.TagResponse
import spot.safety.ssmobile.data.model.UserResponse

interface SafetySpotApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<String>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<String>

    @POST("auth/logout")
    suspend fun logout(@Body request: LogoutRequest): Response<String>

    @GET("api/v1/images")
    suspend fun getImages(@Query("category") category: String? = null): List<ImageResponse>

    @GET("api/v1/images/{id}")
    suspend fun getImage(@Path("id") id: Long): ImageResponse

    @POST("api/v1/images/{imageId}/tag")
    suspend fun submitTag(@Path("imageId") imageId: Long, @Body request: SubmitTagRequest): TagResponse

    @GET("api/v1/progress")
    suspend fun getProgressHistory(): List<ProgressEntryResponse>

    @GET("api/v1/progress/summary")
    suspend fun getProgressSummary(): ProgressSummaryResponse

    @GET("api/v1/users/{id}")
    suspend fun getUser(@Path("id") id: Long): UserResponse

    @POST("leaderboard/schoolClass")
    suspend fun getLeaderboard(@Body request: LeaderboardRequest): LeaderboardResponse
}
