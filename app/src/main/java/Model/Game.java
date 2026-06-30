package Model;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Game {
    private int time;
    private int bombs;
    private int flags;
    private String difficulty;
    private boolean firstHit;
    private boolean win;
    private boolean lose;
    private int[][] values; // 0-8, -1 means bomb -2 bomb kích hoạt
    private boolean[][] revealed;
    private boolean[][] flagged;
    private int hintsUsed = 0;

    public void setUpGame(int row, int column, int bombs, String diff) {
        this.bombs = bombs;
        this.flags = bombs;
        this.difficulty = diff;
        this.firstHit = true;
        values = new int[row][column];
        revealed = new boolean[row][column];
        flagged = new boolean[row][column];
    }

    public void setUpBombs(int firstRow, int firstCol) {
        this.firstHit = false;
        Random random = new Random();
        int placedBombs = 0;
        int rows = values.length;
        int cols = values[0].length;
        int row, col;
        while (placedBombs < bombs) {
            row = random.nextInt(rows);
            col = random.nextInt(cols);
            // has bomb
            if (values[row][col] == -1)
                continue;
            // in safe zone
            if (Math.abs(row - firstRow) <= 2 && Math.abs(col - firstCol) <= 2)
                continue;
            // set bomb
            values[row][col] = -1; // bomb
            placedBombs++;
        }
        calculateNumbers();
    }

    private void calculateNumbers() {
        int rows = values.length;
        int cols = values[0].length;
        int count;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (values[row][col] == -1)
                    continue;
                count = 0;
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if (i == 0 && j == 0)
                            continue;
                        int nearRow = row + i;
                        int nearCol = col + j;
                        if (nearRow >= 0 && nearRow < rows && nearCol >= 0 && nearCol < cols && values[nearRow][nearCol] == -1)
                            count++;
                    }
                }
                values[row][col] = count;
            }
        }
    }

    public void win() {
        this.win=true; // xác nhận trạng thái thắng của game
        if (Session.user != null) {
            // update user stats;
            Session.userStats.updateWin(revealed.length * revealed[0].length - this.bombs);
            // achievement
            AchievementManager am = new AchievementManager();
            am.checkAchievements(Session.user.uid, difficulty, time);
            handleEndGameRewards(true);
        }
    }
    private int calRevealedTiles() {
        int total = 0;
        for (int i = 0; i < revealed.length; i++) {
            for (int j = 0; j < revealed[0].length; j++) {
                if (revealed[i][j])
                    total++;
            }
        }
        return total - 1;
    }

    public void hitTile(int row, int col) {
        if (lose || win)
            return;
        // Không mở nếu đã mở hoặc đã cắm cờ
        if (revealed[row][col] || flagged[row][col])
            return;
        // Trúng bomb
        if (values[row][col] == -1) {
            revealed[row][col] = true;
            values[row][col] = -2;
            revealAllBombs();
            lose = true;
            handleEndGameRewards(false);
            return;
        }
        // Mở ô
        reveal(row, col);
        // Kiểm tra thắng
        checkWin();
    }

    public void chord(int row, int col) {
        if (lose || win)
            return;

        // Chỉ áp dụng cho ô đã mở và là ô số
        if (!revealed[row][col])
            return;
        if (values[row][col] <= 0)
            return;
        int flagCount = 0;

        // Đếm số cờ xung quanh
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0)
                    continue;
                int r = row + i;
                int c = col + j;
                if (r < 0 || r >= values.length || c < 0 || c >= values[0].length)
                    continue;
                if (flagged[r][c])
                    flagCount++;
            }
        }

        // Chưa đủ cờ
        if (flagCount != values[row][col])
            return;

        // Mở các ô chưa mở và chưa cắm cờ
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {

                if (i == 0 && j == 0)
                    continue;

                int r = row + i;
                int c = col + j;

                if (r < 0 || r >= values.length || c < 0 || c >= values[0].length)
                    continue;

                if (revealed[r][c] || flagged[r][c])
                    continue;

                // Nếu có bom không được cắm cờ -> thua
                if (values[r][c] == -1) {
                    revealed[r][c] = true;
                    revealAllBombs();
                    lose = true;
                    handleEndGameRewards(false);
                    return;
                }
                reveal(r, c);
            }
        }
        checkWin();
    }

    private void reveal(int row, int col) {
        int rows = values.length;
        int cols = values[0].length;
        if (row < 0 || row >= rows || col < 0 || col >= cols)
            return;

        if (revealed[row][col])
            return;

        if (flagged[row][col])
            return;

        revealed[row][col] = true;

        // Có số thì dừng
        if (values[row][col] != 0)
            return;

        // Mở 8 ô xung quanh
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0)
                    continue;
                reveal(row + i, col + j);
            }
        }
    }

    private void checkWin() {
        int opened = 0;
        for (int i = 0; i < revealed.length; i++) {
            for (int j = 0; j < revealed[0].length; j++) {

                if (revealed[i][j])
                    opened++;
            }
        }

        if (opened == revealed.length * revealed[0].length - bombs) {
            win();
        }
    }

    public void toggleFlag(int row, int col) {
        if (lose || win)
            return;
        if (revealed[row][col])
            return;

        if (flagged[row][col]) {
            flagged[row][col] = false;
            flags++;
        } else {

            if (flags == 0)
                return;

            flagged[row][col] = true;
            flags--;
        }
    }
    public void revealAllBombs() {
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[0].length; j++) {

                // Hiện tất cả bom chưa được mở
                if (values[i][j] == -1) {
                    revealed[i][j] = true;
                }

                // Cắm cờ sai
                if (flagged[i][j] && values[i][j] != -1) {
                    values[i][j] = -3;
                    flagged[i][j] = false;   // bỏ cờ
                    revealed[i][j] = true;
                }
            }
        }
    }
    public int getTime() {
        return time;
    }

    public void increaseTime() {
        this.time++;
    }

    public boolean isFirstHit() {
        return firstHit;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public boolean isWin() {
        return win;
    }

    public boolean isLose() {
        return lose;
    }

    public boolean isRevealed(int row, int col) {
        return revealed[row][col];
    }

    public boolean isFlagged(int row, int col) {
        return flagged[row][col];
    }

    public int getValue(int row, int col) {
        return values[row][col];
    }

    public void useHint() {
        this.hintsUsed++;
    }

    public int calculateScore() {
        int score = 0;
        int rows = values.length;
        int cols = values[0].length;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (flagged[r][c]) {
                    if (values[r][c] == -1) {
                        score += 50; // Lá cờ đúng
                    } else {
                        score -= 20; // Lá cờ sai
                    }
                }
            }
        }

        score -= (hintsUsed * 100); // Mỗi hint trừ 100 điểm
        return Math.max(0, score); // Điểm không âm
    }

    public int[] getHint() {
        if (lose || win || firstHit) return null;
        List<int[]> safeTiles = new ArrayList<>();
        for (int r = 0; r < values.length; r++) {
            for (int c = 0; c < values[0].length; c++) {
                if (!revealed[r][c] && values[r][c] != -1 && !flagged[r][c]) {
                    safeTiles.add(new int[]{r, c});
                }
            }
        }
        if (safeTiles.isEmpty()) return null;
        return safeTiles.get(new Random().nextInt(safeTiles.size()));
    }

    public void handleEndGameRewards(boolean isWon) {
        // Người dùng phải đăng nhập
        if (Session.user == null || Session.user.uid == null) {
            Log.d("GameFirestore", "Người chơi chưa đăng nhập, không thể lưu kết quả.");
            return;
        }
        String uid = Session.user.uid;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //  Tính toán tiền xu thưởng
        int coinReward = 0;
        if (isWon) {
            coinReward = (this.bombs * 10) + 50;
        } else {
            int correctFlags = 0;
            if (values != null && flagged != null) {
                for (int i = 0; i < values.length; i++) {
                    for (int j = 0; j < values[0].length; j++) {
                        if (values[i][j] == -1 && flagged[i][j]) {
                            correctFlags++;
                        }
                    }
                }
            }
            coinReward = correctFlags * 10;
        }

        //  Cập nhật tiền lên Firestore
        if (coinReward > 0) {
            Session.coins += coinReward;
            java.util.Map<String, Object> coinData = new java.util.HashMap<>();
            coinData.put("coins", Session.coins);

            db.collection("UserCoins").document(uid).set(coinData)
                    .addOnSuccessListener(aVoid -> Log.d("GameFirestore", "Đã update lại tiền trên Firestore!"));
        }

        //  Phần xử lý kỷ lục / bảng xếp hạng khi thắng cuộc
        if (isWon) {

            int score = calculateScore();

            String diffPath = this.difficulty != null ? this.difficulty.toLowerCase() : "easy";

            com.google.firebase.firestore.DocumentReference leaderboardRef = db.collection("Leaderboards")
                    .document("classic")
                    .collection(diffPath)
                    .document(uid);

            // 4. Kiểm tra xem user đã có điểm kỷ lục trước đó chưa
            leaderboardRef.get().addOnSuccessListener(documentSnapshot -> {
                boolean shouldUpdate = false;

                if (documentSnapshot.exists()) {
                    Long currentRecord = documentSnapshot.getLong("score");
                    if (currentRecord == null || score > currentRecord) {
                        shouldUpdate = true; // Điểm mới cao hơn điểm cũ -> Cập nhật
                    }
                } else {
                    shouldUpdate = true;
                }

                if (shouldUpdate) {
                    java.util.Map<String, Object> leaderboardData = new java.util.HashMap<>();
                    leaderboardData.put("uid", uid);
                    leaderboardData.put("score", score);
                    leaderboardData.put("time", this.time);
                    leaderboardData.put("timestamp", com.google.firebase.Timestamp.now());

                    // Lấy tên của user đang đăng nhập
                    leaderboardData.put("username", Session.user.getName());

                    leaderboardRef.set(leaderboardData)
                            .addOnSuccessListener(aVoid -> Log.d("GameFirestore", "Đã phá kỷ lục! Đã cập nhật điểm số mới lên Leaderboard: " + score))
                            .addOnFailureListener(e -> Log.e("GameFirestore", "Lỗi cập nhật Leaderboard: " + e.getMessage()));
                } else {
                    Log.d("GameFirestore", "Điểm số (" + score + ") không cao hơn điểm kỷ lục cũ");
                }
            }).addOnFailureListener(e -> Log.e("GameFirestore", "Không thể kiểm tra điểm kỷ lục cũ: " + e.getMessage()));

            
        }
    }

    private void saveLeaderboard(String difficulty, int score, int time) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection("Leaderboards")
                .document("classic")
                .collection(difficulty)
                .document(user.getUid());
        ref.get().addOnSuccessListener(document -> {
            Long oldCompletedTime = document.getLong("completedTime");
            if (!document.exists() || oldCompletedTime == null || time < oldCompletedTime) {
                Map<String, Object> data = new HashMap<>();
                data.put("userId", user.getUid());
                data.put("username", Session.user.name);
                data.put("score", score);
                data.put("completedTime", time);
                data.put("completedAt", FieldValue.serverTimestamp());
                ref.set(data);
            }
        });
    }

    public int calScore() {
        return 0;
    }
}

