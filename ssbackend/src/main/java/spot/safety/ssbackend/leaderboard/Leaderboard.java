package spot.safety.ssbackend.leaderboard;

import jakarta.persistence.*;
import lombok.Data;
import org.apache.logging.log4j.util.PropertySource;
import spot.safety.ssbackend.help.sortEntries;
import spot.safety.ssbackend.school.ClassGroup;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Entity
@Data
public class Leaderboard {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long Id;

    @OneToOne
    private ClassGroup classGroup;

    @OneToMany
    private List<LeaderboardEntry> entries;

    public Leaderboard(ClassGroup classGroup, List<LeaderboardEntry> entries) {
        this.classGroup = classGroup;
        this.entries = entries;
    }

    public Leaderboard() {
    }

    public void refresh() {
        Comparator comp = new sortEntries();
        entries.sort(comp);
        for(int i = 0; i < entries.size(); i++) {
            entries.get(i).setRank(i + 1);
        }
    }

    public List<LeaderboardEntry> getTopN(int n) {
        refresh();
        return entries.subList(0, n);
    }



}

