package Model;

import java.util.Random;

public class Game {
    private int time;
    private int bombs;
    private int flags;
    private boolean firstHit;
    private int[][] values; // 0-8, -1 means bomb
    private boolean[][] revealed;
    private boolean[][] flagged;
    public void setUpGame(int row, int column, int bombs) {
        this.bombs = bombs;
        this.flags = bombs;
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
                        if (i == 0 && j ==0)
                            continue;
                        int nearRow = row + i;
                        int nearCol = row + j;
                        if (nearRow >= 0 && nearRow < rows && nearCol >= 0 && nearCol < cols && values[nearRow][nearCol] == -1)
                            count++;
                    }
                }
                values[row][col] = count;
            }
        }
    }

    public void hitTile(int row, int column) {

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
}
