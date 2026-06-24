package spot.safety.ssmobile.data.repository

import spot.safety.ssmobile.data.model.ImageResponse
import spot.safety.ssmobile.data.model.SubmitTagRequest
import spot.safety.ssmobile.data.model.TagResponse
import spot.safety.ssmobile.data.model.TagValue
import spot.safety.ssmobile.data.network.SafetySpotApi

class ImageRepository(private val api: SafetySpotApi) {

    suspend fun getImages(category: String? = null): Result<List<ImageResponse>> = runCatching {
        api.getImages(category)
    }

    suspend fun getImage(id: Long): Result<ImageResponse> = runCatching {
        api.getImage(id)
    }

    suspend fun submitTag(imageId: Long, tag: TagValue): Result<TagResponse> = runCatching {
        api.submitTag(imageId, SubmitTagRequest(tag))
    }
}
