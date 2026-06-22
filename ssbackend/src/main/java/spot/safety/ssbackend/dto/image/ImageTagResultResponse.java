package spot.safety.ssbackend.dto.image;

import spot.safety.ssbackend.model.TagValue;

import java.time.Instant;

public record ImageTagResultResponse(
        Long studentId,
        String username,
        TagValue tag,
        boolean correct,
        Instant taggedAt
) {
}
