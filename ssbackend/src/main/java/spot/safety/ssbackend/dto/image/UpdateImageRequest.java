package spot.safety.ssbackend.dto.image;

import jakarta.validation.constraints.Size;
import spot.safety.ssbackend.model.TagValue;

public record UpdateImageRequest(
        @Size(max = 100) String title,
        @Size(max = 500) String description,
        String imageUrl,
        @Size(max = 50) String category,
        TagValue correctTag
) {
}
