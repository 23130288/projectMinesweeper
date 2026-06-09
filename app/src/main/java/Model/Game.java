package Model;

public class Game {
    private int bombs;
    private int flags;
    private final int UNREVEALED_TILE = -1;
    private int[][] tiles;

    public void setUpGame(int row, int column, int bombs) {
        tiles = new int[row][column];
        this.bombs = bombs;
        this.flags = bombs;
    }

    public void setUpBombs() {

    }
}
