package com.example.chess;

public class Tile {
    private TileType tileType;
    private final boolean isBlack;
    private boolean isLighted = false;

    private final HighLightController highLightController;
    private final TileTypeChanger tileTypeChanger;

    public Tile(boolean isBlack, TileType tileType, HighLightController highLightController, TileTypeChanger tileTypeChanger) {
        this.isBlack = isBlack;
        this.tileType = tileType;
        this.highLightController = highLightController;
        this.tileTypeChanger = tileTypeChanger;
        highLightController.setHighLighted(false);
        tileTypeChanger.change(tileType);
    }

    public void setTileType(TileType tileType) {
        this.tileType = tileType;
        tileTypeChanger.change(tileType);
    }

    public TileType getTileType() {
        return tileType;
    }

    public boolean isBlack() {
        return isBlack;
    }

    public boolean isLighted() {
        return isLighted;
    }

    void setHighLighted(boolean isHighLighted) {
        isLighted = isHighLighted;
        highLightController.setHighLighted(isHighLighted);
    }
}
