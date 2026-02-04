package de.joshicodes.polycore.util.packet;

public class PlayerDTO {
    public int[][] board;
    public boolean alive;
    public int cx, cy;
    public int colorId;

    public PlayerDTO(int[][] b, boolean a, int x, int y, int c) {
        this.board = b; this.alive = a; this.cx = x; this.cy = y; this.colorId = c;
    }
}
