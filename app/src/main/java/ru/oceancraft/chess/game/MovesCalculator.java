package ru.oceancraft.chess.game;

public interface MovesCalculator {
    Position[][] calculateFor(int x, int y);
}
