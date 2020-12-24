package ru.oceancraft.chess.model;

public interface MovesCalculator {
    Position[][] calculateFor(int x, int y);
}
