package spot.safety.ssbackend.progress;

import spot.safety.ssbackend.dto.progress.ProgressEntryResponse;
import spot.safety.ssbackend.dto.progress.ProgressSummaryResponse;
import spot.safety.ssbackend.user.UserPrincipal;

import java.util.List;

public interface ProgressService {
    List<ProgressEntryResponse> getHistory(UserPrincipal actor);

    ProgressSummaryResponse getSummary(UserPrincipal actor);
}
