package spot.safety.ssbackend.leaderboard;

import jakarta.persistence.*;
import lombok.Data;
import spot.safety.ssbackend.user.User;

@Entity
@Data
public class LeaderboardEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @ManyToOne
    private User user;

    private int totalPoints;
    private int rank;

    public LeaderboardEntry(User user, int totalPoints, int rank) {
        this.user = user;
        this.totalPoints = totalPoints;
        this.rank = rank;
    }

    public LeaderboardEntry() {
    }

    public int addPoints(int points) {
        this.totalPoints = totalPoints + points;
        return totalPoints;
    }

    public int subtractPoints(int points) {
        this.totalPoints = totalPoints - points;
        return totalPoints;
    }
}
