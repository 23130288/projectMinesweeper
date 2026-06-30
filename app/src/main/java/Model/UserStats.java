package Model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class UserStats {
    private int gamesPlayed;
    private int gamesWon;
    private int totalTilesOpened;
    public UserStats() {
    }

    public UserStats(int gamesPlayed, int gamesWon, int totalTilesOpened) {
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
        this.totalTilesOpened = totalTilesOpened;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    public int getTotalTilesOpened() {
        return totalTilesOpened;
    }

    public void setTotalTilesOpened(int totalTilesOpened) {
        this.totalTilesOpened = totalTilesOpened;
    }

    public void updateWin(int revealed) {
        this.gamesPlayed++;
        this.gamesWon++;
        this.totalTilesOpened += revealed;
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .update(
                        "gamesPlayed", this.gamesPlayed,
                        "gamesWon", this.gamesWon,
                        "totalTilesOpened", this.totalTilesOpened
                );
    }
}
