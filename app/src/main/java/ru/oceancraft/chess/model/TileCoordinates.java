package ru.oceancraft.chess.model;

public class TileCoordinates {
    private final int x;
    private final int y;

    private final boolean whiteView;

    public TileCoordinates(int x, int y, boolean whiteView) {
        this.x = x;
        this.y = y;
        this.whiteView = whiteView;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getTextX() {
        return ((whiteView && y == 7) || (!whiteView && y == 0)) ? String.valueOf((char) (((int) 'a') + x)) : "";
    }

    public String getTextY() {
        return ((whiteView && x == 0) || (!whiteView && x == 7)) ? String.valueOf(8 - y) : "";
    }
}
