package com.example.chess.game;

public interface MovesCalculator {
    Position[][] calculateFor(int x, int y);
}
