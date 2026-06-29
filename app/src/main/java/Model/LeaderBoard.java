package Model;

import com.google.firebase.Timestamp;

public class LeaderBoard {
    private String userId;
    private int score;
    private int completedTime;
    private Timestamp completedAt;

    public LeaderBoard() {
    }

    public LeaderBoard(String userId, int score, int completedTime, Timestamp completedAt) {
        this.userId = userId;
        this.score = score;
        this.completedTime = completedTime;
        this.completedAt = completedAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(int completedTime) {
        this.completedTime = completedTime;
    }

    public Timestamp getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Timestamp completedAt) {
        this.completedAt = completedAt;
    }
}
