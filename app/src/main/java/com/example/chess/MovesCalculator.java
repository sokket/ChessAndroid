package com.example.chess;

import java.util.List;

public interface MovesCalculator {
    List<Position> calculateFor(int x, int y, IntersectionChecker intersectionChecker);
}
