package spot.safety.ssbackend.dto.stats;

import java.util.List;

public record ClassStatsResponse(
        Long classId,
        String className,
        int totalImages,
        int studentCount,
        List<StudentStatEntry> students
) {
}
