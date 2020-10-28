package ru.oceancraft.chess.game;

public interface MoveListener {
    void onMakeMove(int xOld, int yOld, int xNew, int yNew);
}
