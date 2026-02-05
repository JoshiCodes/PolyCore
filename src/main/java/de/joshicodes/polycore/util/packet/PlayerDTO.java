package de.joshicodes.polycore.util.packet;

import de.joshicodes.polycore.game.Shape;

public class PlayerDTO {

    public int[][] board;
    public boolean alive;
    public int cx, cy;
    public int colorId;
    public int[][] shape;
    public PieceDTO nextPiece;
    public PieceDTO holdPiece;

    public PlayerDTO(int[][] b, boolean a, int x, int y, int c, int[][] shape, Shape nextPiece, Shape holdPiece) {
        this.board = b;
        this.alive = a;
        this.cx = x;
        this.cy = y;
        this.colorId = c;
        this.shape = shape;
        this.nextPiece = new PieceDTO(nextPiece);
        if(holdPiece != null)
            this.holdPiece = new PieceDTO(holdPiece);
    }

}
