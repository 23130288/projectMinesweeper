package Model;

import java.util.Random;

public class Game {
    private int time;
    private int bombs;
    private int flags;
    private String difficulty;
    private boolean firstHit;
    private boolean win;
    private boolean lose;
    private int[][] values; // 0-8, -1 means bomb
    private boolean[][] revealed;
    private boolean[][] flagged;

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
        // todo create win condition

        // update user stats;


        // achievement
        AchievementManager am = new AchievementManager();
        am.checkAchievements(Session.user.uid, difficulty, time);
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
            lose = true;
            return;
        }

        // Mở ô
        reveal(row, col);

        // Kiểm tra thắng
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
            win = true;
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

    public boolean isWin() {return win;}

    public boolean isLose() {return lose;}

    public boolean isRevealed(int row, int col) {return revealed[row][col];}

    public boolean isFlagged(int row, int col) {return flagged[row][col];}

    public int getValue(int row, int col) {return values[row][col];}
}
