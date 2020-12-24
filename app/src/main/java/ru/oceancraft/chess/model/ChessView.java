package ru.oceancraft.chess.model;

public interface ChessView {
    void onHighLight(int x, int y, boolean isHighLighted, boolean isBlack);

    void onChangeTile(int x, int y, TileType type);

    void setOnPressListener(OnPressListener onPressListener);

    void setResetOnPressListener(ResetOnPressListener resetOnPressListener);

    void onNewLogLine(LogLine logLine);

    void cleanLog();

    void onMoveFinished(boolean whiteTurn);

    void onCheck();

    void onCheckmate();
}
