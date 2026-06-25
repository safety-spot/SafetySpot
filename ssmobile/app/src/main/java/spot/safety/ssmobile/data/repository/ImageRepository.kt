package spot.safety.ssmobile.data.repository

import android.content.Context
import coil.request.ImageRequest
import spot.safety.ssmobile.data.TokenStore
import spot.safety.ssmobile.data.model.ImageResponse
import spot.safety.ssmobile.data.model.SubmitTagRequest
import spot.safety.ssmobile.data.model.TagResponse
import spot.safety.ssmobile.data.model.TagValue
import spot.safety.ssmobile.data.network.SafetySpotApi

class ImageRepository(private val api: SafetySpotApi, private val tokenStore: TokenStore) {

    suspend fun getImages(category: String? = null): Result<List<ImageResponse>> = runCatching {
        api.getImages(category)
    }

    suspend fun getImage(id: Long): Result<ImageResponse> = runCatching {
        api.getImage(id)
    }

    fun requestImageData(context: Context, imageUrl: String): ImageRequest {
        val tok = tokenStore.token;
        return ImageRequest.Builder(context)
            .data(imageUrl)
            .addHeader("Authorization", "Bearer $tok")
            .crossfade(true)
            .build();
    }

    suspend fun submitTag(imageId: Long, tag: TagValue): Result<TagResponse> = runCatching {
        api.submitTag(imageId, SubmitTagRequest(tag))
    }
}
