package spot.safety.ssbackend.dto.image;

import com.fasterxml.jackson.annotation.JsonInclude;
import spot.safety.ssbackend.model.TagValue;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ImageResponse(
        Long id,
        String title,
        String description,
        String imageUrl,
        String category,
        TagValue correctTag,
        Long uploadedById,
        String uploadedByUsername,
        Instant createdAt,
        Instant updatedAt
) {
}
