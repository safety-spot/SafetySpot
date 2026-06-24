package spot.safety.ssmobile.data.model

data class LoginRequest(val username: String, val password: String)
data class RegisterRequest(val username: String, val password: String, val schoolName: String, val role: String)
data class LogoutRequest(val token: String)

enum class TagValue {
    DANGEROUS,
    SAFE
}

data class ImageResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val imageUrl: String?,
    val category: String?,
    val correctTag: TagValue?,
    val uploadedById: Long?,
    val uploadedByUsername: String?,
    val createdAt: String?,
    val updatedAt: String?
)

data class SubmitTagRequest(val tag: TagValue)

data class TagResponse(
    val imageId: Long,
    val tag: TagValue,
    val correct: Boolean,
    val feedback: String?,
    val taggedAt: String?
)

data class ProgressSummaryResponse(
    val totalTagged: Long,
    val correctCount: Long,
    val accuracyPercent: Double
)

data class ProgressEntryResponse(
    val imageId: Long,
    val imageTitle: String?,
    val category: String?,
    val studentTag: TagValue?,
    val correct: Boolean,
    val taggedAt: String?
)

data class UserResponse(
    val id: Long,
    val username: String,
    val role: String,
    val schoolId: Long?,
    val classId: Long?,
    val active: Boolean,
    val createdAt: String?,
    val lastLoginAt: String?
)

data class LeaderboardRequest(val schoolClass: SchoolClassRef)
data class SchoolClassRef(val id: Long)

data class LeaderboardResponse(
    val id: Long,
    val entries: List<LeaderboardEntryResponse>
)

data class LeaderboardEntryResponse(
    val id: Long,
    val user: UserBrief?,
    val totalPoints: Int,
    val rank: Int
)

data class UserBrief(
    val id: Long,
    val username: String
)
