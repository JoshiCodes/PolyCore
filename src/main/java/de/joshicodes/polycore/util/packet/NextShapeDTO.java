package de.joshicodes.polycore.util.packet;

import de.joshicodes.polycore.game.Shape;

public class NextShapeDTO {

    public int[][] shape;
    public int colorId;

    public NextShapeDTO(Shape nextShape) {
        this.shape = nextShape.shape;
        this.colorId = nextShape.colorId;
    }

}
