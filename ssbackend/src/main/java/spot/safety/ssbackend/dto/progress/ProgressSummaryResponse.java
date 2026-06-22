package spot.safety.ssbackend.dto.progress;

public record ProgressSummaryResponse(
        long totalTagged,
        long correctCount,
        double accuracyPercent
) {
}
