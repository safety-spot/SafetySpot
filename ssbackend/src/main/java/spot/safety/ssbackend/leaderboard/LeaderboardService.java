package spot.safety.ssbackend.leaderboard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spot.safety.ssbackend.school.SchoolClass;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class LeaderboardService {
    private final LeaderboardRepository leaderboardRepository;

    public void newLeaderboard(SchoolClass schoolClass) {
        Leaderboard leaderboard = new Leaderboard(
                schoolClass,
                new ArrayList<LeaderboardEntry>()
        );

        leaderboardRepository.saveAndFlush(leaderboard);
    }

    public void saveLeaderboard(Leaderboard leaderboard) {
        leaderboardRepository.saveAndFlush(leaderboard);
    }

    public Leaderboard getLeaderboard(SchoolClass schoolClass) {
        return leaderboardRepository.findBySchoolClass(schoolClass).getFirst();
    }
}
