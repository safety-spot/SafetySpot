package spot.safety.ssbackend.leaderboard;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spot.safety.ssbackend.dto.LeaderboardRequest;

@RestController
@RequestMapping("/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {
    private final LeaderboardService leaderboardService;

    @PostMapping("/classGroup")
    public ResponseEntity<Leaderboard> getLeaderboard(@RequestBody LeaderboardRequest request) {
       Leaderboard leaderboard = leaderboardService.getLeaderboard(request.schoolClass());
       return ResponseEntity
               .status(HttpStatus.OK)
               .body(leaderboard);
    }

    @PostMapping("/new")
    public ResponseEntity<String> newLeaberboard(@RequestBody LeaderboardRequest request) {
        leaderboardService.newLeaderboard(request.schoolClass());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Creation successful");
    }


}
