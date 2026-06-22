package spot.safety.ssbackend.leaderboard;

import jakarta.persistence.*;
import lombok.Data;
import spot.safety.ssbackend.help.sortEntries;
import spot.safety.ssbackend.school.SchoolClass;

import java.util.Comparator;
import java.util.List;

@Entity
@Data
public class Leaderboard {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @OneToOne
    private SchoolClass schoolClass;

    @OneToMany
    private List<LeaderboardEntry> entries;

    public Leaderboard(SchoolClass schoolClass, List<LeaderboardEntry> entries) {
        this.schoolClass = schoolClass;
        this.entries = entries;
    }

    public Leaderboard() {
    }

    public void refresh() {
        Comparator comp = new sortEntries();
        entries.sort(comp);
        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).setRank(i + 1);
        }
    }

    public List<LeaderboardEntry> getTopN(int n) {
        refresh();
        int limit = Math.min(Math.max(n, 0), entries.size());
        return entries.subList(0, limit);
    }
}
