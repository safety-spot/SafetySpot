package spot.safety.ssbackend.dto.stats;

public record StudentStatEntry(
        Long userId,
        String username,
        long totalTagged,
        long correctCount,
        double accuracyPercent
) {
}
