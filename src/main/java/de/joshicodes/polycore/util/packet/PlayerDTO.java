package de.joshicodes.polycore.util.packet;

import de.joshicodes.polycore.game.Shape;

public class PlayerDTO {

    public int[][] board;
    public boolean alive;
    public int cx, cy;
    public int colorId;
    public int[][] shape;
    public NextShapeDTO nextPiece;

    public PlayerDTO(int[][] b, boolean a, int x, int y, int c, int[][] shape, Shape nextPiece) {
        this.board = b;
        this.alive = a;
        this.cx = x;
        this.cy = y;
        this.colorId = c;
        this.shape = shape;
        this.nextPiece = new NextShapeDTO(nextPiece);
    }

}
