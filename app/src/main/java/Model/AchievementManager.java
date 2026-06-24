package Model;

public class AchievementManager {
    public void checkAchievements(String uid, String difficulty, int timeSeconds) {
        UserStats stats = new UserStats();
        if (stats.getGamesWon() >= 1) {
            unlockAchievement(uid, "first_win");
        }
        if (stats.getGamesWon() >= 10) {
            unlockAchievement(uid, "win_10");
        }
        if (difficulty.equals("easy") && timeSeconds < 20) {
            unlockAchievement(uid, "speed_easy");
        }
        if (difficulty.equals("medium") && timeSeconds < 60) {
            unlockAchievement(uid, "speed_medium");
        }
        if (difficulty.equals("hard") && timeSeconds < 150) {
            unlockAchievement(uid, "speed_hard");
        }
        if (difficulty.equals("extreme")  && timeSeconds < 300) {
            unlockAchievement(uid, "speed_extreme");
        }
    }

    private void unlockAchievement(String uid, String aid) {

    }
}
