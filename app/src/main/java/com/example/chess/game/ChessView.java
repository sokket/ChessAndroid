package com.example.chess.game;

public interface ChessView {
    void onHighLight(int x, int y, boolean isHighLighted, boolean isBlack);
    void onChangeTile(int x, int y, TileType type);
    void setOnPressListener(OnPressListener onPressListener);
}
