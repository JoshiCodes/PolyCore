package de.joshicodes.polycore.util.packet;

public class PlayerDTO {

    public int[][] board;
    public boolean alive;
    public int cx, cy;
    public int colorId;
    public int[][] shape;

    public PlayerDTO(int[][] b, boolean a, int x, int y, int c, int[][] shape) {
        this.board = b;
        this.alive = a;
        this.cx = x;
        this.cy = y;
        this.colorId = c;
        this.shape = shape;
    }
}
