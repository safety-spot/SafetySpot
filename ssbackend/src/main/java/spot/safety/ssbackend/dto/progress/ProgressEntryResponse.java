package spot.safety.ssbackend.dto.progress;

import spot.safety.ssbackend.model.TagValue;

import java.time.Instant;

public record ProgressEntryResponse(
        Long imageId,
        String imageTitle,
        String category,
        TagValue studentTag,
        boolean correct,
        Instant taggedAt
) {
}
