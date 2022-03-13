package ru.oceancraft.chess.model;

public interface MoveListener {
    void onMakeMove(int xOld, int yOld, int xNew, int yNew);
}
