package spot.safety.ssbackend.help;

import spot.safety.ssbackend.leaderboard.LeaderboardEntry;

import java.util.Comparator;

public class sortEntries implements Comparator {
    public int compare(Object obj1, Object obj2) {
        LeaderboardEntry entry1 = (LeaderboardEntry) obj1;
        LeaderboardEntry entry2 = (LeaderboardEntry) obj2;

        if(entry1.getTotalPoints() > entry2.getTotalPoints()) return -1;

        if(entry1.getTotalPoints() < entry2.getTotalPoints()) return 1;

        return 0;
    }
}
