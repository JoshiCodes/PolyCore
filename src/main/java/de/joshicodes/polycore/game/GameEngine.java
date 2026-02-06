package de.joshicodes.polycore.game;

import java.util.Random;

public class GameEngine {

    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 20;

    private int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
    private boolean holdUsed = false;
    private Shape currentPiece;
    private Shape nextPiece;
    private Shape holdPiece;
    private int[][] currentShape;

    private int pendingGarbage = 0;

    private int currentX, currentY, currentDimension;
    private double points = 0;

    private boolean gameOver = false;

    private Random rand = new Random();

    public GameEngine() {
        spawnPiece();
    }

    public void queueGarbage(final int lines) {
        pendingGarbage += lines;
    }

    /**
     * Returns the number of lines cleared in this tick.
     * or -1 if the game is over.
     * If the lines cleared is 0, no lines were cleared or the piece is still falling.
     * @return number of lines cleared or -1 if game over
     */
    public int tick() {
        if(gameOver) return -1;
        if(isValidMove(currentShape, currentX, currentY + 1)) {
            // Can keep falling
            currentY++;
            return 0;
        }
        // place piece
        int lines = placePiece();
        return lines;
    }

    public void rotate() {
        if(currentDimension == 2) return; // 2x2 pieces don't need to rotate
        int[][] rotated = new int[4][2];
        for(int i = 0; i < 4; i++) {
            rotated[i][0] = currentDimension - 1 - currentShape[i][1];
            rotated[i][1] = currentShape[i][0];
        }
        if(isValidMove(rotated, currentX, currentY)) {
            currentShape = rotated;
        }
    }

    /**
     * Moves the current piece by dx if valid.
     * Use negative dx to move left, positive to move right.
     * @param dx the delta x to move
     */
    public void move(int dx) {
        if(isValidMove(currentShape, currentX + dx, currentY)) { currentX += dx; }
    }

    public void down() {
        if(isValidMove(currentShape, currentX, currentY + 1)) {
            currentY++;
        }
    }

    public void drop() {
        while(isValidMove(currentShape, currentX, currentY + 1)) {
            currentY++;
        }
        placePiece();
    }

    public void holdPiece() {
        if(holdUsed) return;
        holdUsed = true;
        if(holdPiece == null) {
            holdPiece = currentPiece;
            spawnPiece();
            return;
        }
        Shape temp = currentPiece;
        currentPiece = holdPiece;
        holdPiece = temp;
        currentShape = currentPiece.shape;
        currentDimension = currentPiece.dimension;
        currentX = BOARD_WIDTH / 2 - (currentDimension / 2);
        currentY = 0; // top
    }

    private int placePiece() {
        lockPiece();
        int lines = checkLines();

        if(lines > 0 && pendingGarbage > 0) {
            // If we cleared lines and have pending garbage, we can reduce the garbage by the cleared lines
            int counter = Math.min(lines, pendingGarbage);
            pendingGarbage -= counter;
            lines -= counter;
            if(lines < 0) {
                lines = 0;
            }
        }

        if(pendingGarbage > 0) {
            addGarbage(pendingGarbage);
            pendingGarbage = 0;
        }

        spawnPiece();
        if(!isValidMove(currentShape, currentX, currentY)) {
            gameOver = true;
            return -1;
        }
        addPoints(lines);
        return lines;
    }

    /**
     * Adds garbage with random gap position.
     */
    public void addGarbage(int lines) {
        addGarbage(lines, rand.nextInt(BOARD_WIDTH));
    }

    public void addGarbage(int lines, int gapX) {
        if(gameOver || lines <= 0) return;

        // Shift existing rows up
        for(int y = 0; y < BOARD_HEIGHT - lines; y++) {
            board[y] = board[y + lines].clone();
        }

        // Fill bottom rows with garbage (colorId 8 = gray garbage)
        for(int y = BOARD_HEIGHT - lines; y < BOARD_HEIGHT; y++) {
            for(int x = 0; x < BOARD_WIDTH; x++) {
                board[y][x] = (x == gapX) ? 0 : 8; // Gap at gapX, garbage elsewhere
            }
        }

        // Check if current piece is now invalid (pushed into collision)
        if(!isValidMove(currentShape, currentX, currentY)) {
            // Try to push piece up
            while(!isValidMove(currentShape, currentX, currentY) && currentY > 0) {
                currentY--;
            }
            // If still invalid, game over
            if(!isValidMove(currentShape, currentX, currentY)) {
                gameOver = true;
            }
        }
    }

    private void addPoints(final int lines) {
        if(lines <= 0) {
            return;
        } else {
            points += switch (lines) {
                case 1 -> 100;
                case 2 -> 300;
                case 3 -> 500;
                case 4 -> 800;
                default -> lines * 200; // Should never happen
            };
        }
    }

    private int checkLines() {
        int cleared = 0;
        for(int y = BOARD_HEIGHT - 1; y >= 0; y--) {
            boolean full = true;
            for(int x = 0; x < BOARD_WIDTH; x++) {
                if(board[y][x] == 0) {
                    full = false;
                    break;
                }
            }
            if(full) {
                // Add to counter and move all other lines down
                cleared++;
                for(int dy = y; dy > 0; dy--) {
                    board[dy] = board[dy-1].clone();
                }
                board[0] = new int[BOARD_WIDTH];
                y++;
            }
        }
        return cleared;
    }

    private boolean isValidMove(int[][] shape, int nx, int ny) {
        for(int[] p : shape) {
            int x = nx + p[0];
            int y = ny + p[1];
            if(x < 0 || x >= BOARD_WIDTH || y >= BOARD_HEIGHT) return false;
            if(y >= 0 && board[y][x] != 0) return false;
        }
        return true;
    }

    private void lockPiece() {
        for(int[] p : currentShape) {
            int x = currentX + p[0];
            int y = currentY + p[1];
            if (x >= 0 && x < BOARD_WIDTH && y >= 0 && y < BOARD_HEIGHT) board[y][x] = currentPiece.colorId;
        }
    }

    private void spawnPiece() {
        currentPiece = nextPiece != null ? nextPiece : Shape.values()[rand.nextInt(Shape.values().length)];
        currentShape = currentPiece.shape;
        nextPiece = Shape.values()[rand.nextInt(Shape.values().length)];
        currentDimension = currentPiece.dimension;
        currentX = BOARD_WIDTH / 2 - (currentDimension / 2);
        currentY = 0; // top
        holdUsed = false;
    }

    public void reset() {
        board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        gameOver = false;
        points = 0;
        holdPiece = null;
        nextPiece = null;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getColorId() {
        return currentPiece.colorId;
    }

    public int getCurrentDimension() {
        return currentDimension;
    }

    public int getCurrentX() {
        return currentX;
    }

    public int getCurrentY() {
        return currentY;
    }

    public int[][] getBoard() {
        return board;
    }

    public int[][] getCurrentShape() {
        return currentShape;
    }

    public Shape getCurrentPiece() {
        return currentPiece;
    }

    public Shape getNextPiece() {
        return nextPiece;
    }

    public Shape getHoldPiece() {
        return holdPiece;
    }

    public double getPoints() {
        return points;
    }

}
