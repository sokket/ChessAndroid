package ru.oceancraft.chess.model.listeners;

public interface MoveListener {
    void onMakeMove(int xOld, int yOld, int xNew, int yNew);
}
