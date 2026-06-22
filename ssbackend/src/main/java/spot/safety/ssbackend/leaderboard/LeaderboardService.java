package spot.safety.ssbackend.leaderboard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spot.safety.ssbackend.school.ClassGroup;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class LeaderboardService {
    private final LeaderboardRepository leaderboardRepository;

    public void newLeaderboard(ClassGroup classGroup) {
        Leaderboard leaderboard = new Leaderboard(
                classGroup,
                new ArrayList<LeaderboardEntry>()
        );

        leaderboardRepository.saveAndFlush(leaderboard);
    }

    public void saveLeaderboard(Leaderboard leaderboard) {
        leaderboardRepository.saveAndFlush(leaderboard);
    }

    public Leaderboard getLeaderboard(ClassGroup classGroup) {
        return leaderboardRepository.findByClassGroup(classGroup).getFirst();
    }
}
