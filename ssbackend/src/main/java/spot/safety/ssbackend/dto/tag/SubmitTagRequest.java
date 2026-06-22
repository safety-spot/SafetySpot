package spot.safety.ssbackend.dto.tag;

import jakarta.validation.constraints.NotNull;
import spot.safety.ssbackend.model.TagValue;

public record SubmitTagRequest(
        @NotNull TagValue tag
) {
}
