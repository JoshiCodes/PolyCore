package de.joshicodes.polycore.game;

public enum Shape {

    // I (Cyan) - 4x4 Box
    I(new int[][] { {0, 1},   {1, 1},   {2, 1},   {3, 1} }, 1, 4),

    // J (Blau) - 3x3 Box -> Dreht um den Stein bei (1,1)
    J(new int[][]{{0, 0}, {0, 1}, {1, 1}, {2, 1}}, 2, 3),

    // L (Orange) - 3x3 Box
    L(new int[][]{{2, 0}, {0, 1}, {1, 1}, {2, 1}}, 3, 3),

    // O (Gelb) - 2x2 Box (Rotation ändert hier nichts an der Position)
    O(new int[][]{{0, 0}, {1, 0}, {0, 1}, {1, 1}}, 4, 2),

    // S (Grün) - 3x3 Box
    S(new int[][]{{1, 0}, {2, 0}, {0, 1}, {1, 1}}, 5, 3),

    // T (Lila) - 3x3 Box
    T(new int[][]{{1, 0}, {0, 1}, {1, 1}, {2, 1}}, 6, 3),

    // Z (Rot) - 3x3 Box
    Z(new int[][]{{0, 0}, {1, 0}, {1, 1}, {2, 1}}, 7, 3);

    //              x, y
    public final int[][] shape;
    public final int colorId;
    public final int dimension;

    Shape(final int[][] shape, final int colorId, final int dimension) {
        this.shape = shape;
        this.colorId = colorId;
        this.dimension = dimension; // x * x
    }

}