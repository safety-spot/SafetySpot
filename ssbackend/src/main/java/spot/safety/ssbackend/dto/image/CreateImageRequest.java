package spot.safety.ssbackend.dto.image;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import spot.safety.ssbackend.model.TagValue;

public record CreateImageRequest(
        @NotBlank @Size(max = 100) String title,
        @Size(max = 500) String description,
        @NotBlank String imageUrl,
        @Size(max = 50) String category,
        @NotNull TagValue correctTag
) {
}
