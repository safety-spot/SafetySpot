package spot.safety.ssbackend.stats;

import spot.safety.ssbackend.dto.stats.ClassStatsResponse;
import spot.safety.ssbackend.dto.stats.ImageStatsResponse;
import spot.safety.ssbackend.user.UserPrincipal;

public interface StatsService {
    ClassStatsResponse getClassStats(Long classId, UserPrincipal actor);
    ImageStatsResponse getImageStats(Long imageId, UserPrincipal actor);
}
