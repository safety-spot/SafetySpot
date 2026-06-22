package spot.safety.ssbackend.dto.stats;

import spot.safety.ssbackend.model.TagValue;

public record ImageStatsResponse(
        Long imageId,
        String title,
        TagValue correctTag,
        int totalResponses,
        int correctResponses,
        double correctRate,
        int dangerousCount,
        int safeCount
) {
}
