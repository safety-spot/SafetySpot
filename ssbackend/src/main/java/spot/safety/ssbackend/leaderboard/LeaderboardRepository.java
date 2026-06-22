package spot.safety.ssbackend.leaderboard;

import org.springframework.data.jpa.repository.JpaRepository;
import spot.safety.ssbackend.school.SchoolClass;

import java.util.List;

public interface LeaderboardRepository extends JpaRepository<Leaderboard, Long> {
    List<Leaderboard> findBySchoolClass(SchoolClass schoolClass);
}
