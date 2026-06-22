package spot.safety.ssbackend.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spot.safety.ssbackend.dto.stats.ClassStatsResponse;
import spot.safety.ssbackend.dto.stats.ImageStatsResponse;
import spot.safety.ssbackend.user.SecurityUser;
import spot.safety.ssbackend.user.UserPrincipal;

@RestController
@RequestMapping("/api/v1/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @GetMapping("/class/{classId}")
    public ClassStatsResponse getClassStats(
            @PathVariable Long classId,
            @AuthenticationPrincipal SecurityUser principal) {
        return statsService.getClassStats(classId, UserPrincipal.from(principal.getUser()));
    }

    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @GetMapping("/image/{imageId}")
    public ImageStatsResponse getImageStats(
            @PathVariable Long imageId,
            @AuthenticationPrincipal SecurityUser principal) {
        return statsService.getImageStats(imageId, UserPrincipal.from(principal.getUser()));
    }
}
