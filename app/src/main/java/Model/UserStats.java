package Model;

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
}
