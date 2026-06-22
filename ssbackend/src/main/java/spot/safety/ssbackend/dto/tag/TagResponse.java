package spot.safety.ssbackend.dto.tag;

import spot.safety.ssbackend.model.TagValue;

import java.time.Instant;

public record TagResponse(
        Long imageId,
        TagValue tag,
        boolean correct,
        String feedback,
        Instant taggedAt
) {
}
