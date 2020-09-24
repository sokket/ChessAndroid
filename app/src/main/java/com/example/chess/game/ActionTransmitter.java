package com.example.chess.game;

public interface ActionTransmitter {
    void setOnMakeMoveListener(MoveListener moveListener);
    void makeMove(int xOld, int yOld, int xNew, int yNew);
}
