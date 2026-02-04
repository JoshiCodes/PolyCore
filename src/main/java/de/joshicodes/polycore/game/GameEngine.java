package de.joshicodes.polycore.game;

import java.util.Random;

public class GameEngine {

    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 20;

    private int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
    private Shape currentPiece;
    private int[][] currentShape;

    private int currentX, currentY, currentDimension;

    private boolean gameOver = false;

    private Random rand = new Random();

    public GameEngine() {
        spawnPiece();
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
        // Place piece
        lockPiece();
        int lines = checkLines();
        spawnPiece();
        if(!isValidMove(currentShape, currentX, currentY + 1)) {
            gameOver = true;
        }
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
        currentPiece = Shape.values()[rand.nextInt(Shape.values().length)];
        currentShape = currentPiece.shape;
        currentDimension = currentPiece.dimension;
        currentX = BOARD_WIDTH / 2 - (currentDimension / 2);
        currentY = 0; // top
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

}
