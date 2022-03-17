package ru.oceancraft.chess.model;

import ru.oceancraft.chess.model.listeners.OnPressListener;
import ru.oceancraft.chess.model.listeners.ResetOnPressListener;
import ru.oceancraft.chess.model.listeners.OnPromotionListener;

public interface ChessView {
    void onHighLight(int x, int y, boolean isHighLighted, boolean isBlack);

    void onChangeTile(int x, int y, TileType type);

    void setOnPressListener(OnPressListener onPressListener);

    void setResetOnPressListener(ResetOnPressListener resetOnPressListener);

    void onNewLogLine(LogLine logLine);

    void cleanLog();

    void onLocalGameMoveFinished(boolean whiteTurn);

    void onCheck();

    void onCheckmate();

    void onNetGameMoveFinished(boolean whiteTurn);

    void promotionChoiceRequirement(Position viewPos, boolean isWhiteGame, OnPromotionListener onPromotionListener);
}
