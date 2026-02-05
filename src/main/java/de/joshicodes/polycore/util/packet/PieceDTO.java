package de.joshicodes.polycore.util.packet;

import de.joshicodes.polycore.game.Shape;

public class PieceDTO {

    public int[][] shape;
    public int colorId;

    public PieceDTO(Shape nextShape) {
        this.shape = nextShape.shape;
        this.colorId = nextShape.colorId;
    }

}
