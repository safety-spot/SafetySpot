package spot.safety.ssbackend.leaderboard;

import org.springframework.data.jpa.repository.JpaRepository;
import spot.safety.ssbackend.school.ClassGroup;

import java.util.List;

public interface LeaderboardRepository extends JpaRepository<Leaderboard, Long> {
    List<Leaderboard> findByClassGroup(ClassGroup classGroup);
}
