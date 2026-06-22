package spot.safety.ssbackend.progress;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spot.safety.ssbackend.dto.progress.ProgressEntryResponse;
import spot.safety.ssbackend.dto.progress.ProgressSummaryResponse;
import spot.safety.ssbackend.user.SecurityUser;
import spot.safety.ssbackend.user.UserPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping
    public List<ProgressEntryResponse> getHistory(@AuthenticationPrincipal SecurityUser principal) {
        return progressService.getHistory(UserPrincipal.from(principal.getUser()));
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/summary")
    public ProgressSummaryResponse getSummary(@AuthenticationPrincipal SecurityUser principal) {
        return progressService.getSummary(UserPrincipal.from(principal.getUser()));
    }
}
