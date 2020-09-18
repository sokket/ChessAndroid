package com.example.chess;

import androidx.annotation.DrawableRes;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum TileType {
    LIGHT_BISHOP(R.drawable.ic_chess_blt45, TileType::bishopMoves, true),
    LIGHT_KING(R.drawable.ic_chess_klt45, TileType::kingMoves, true),
    LIGHT_KNIGHT(R.drawable.ic_chess_nlt45, TileType::knightMoves, true),
    LIGHT_PAWN(R.drawable.ic_chess_plt45, TileType::pawnMoves, true),
    LIGHT_QUEEN(R.drawable.ic_chess_qlt45, TileType::queenMoves, true),
    LIGHT_ROOK(R.drawable.ic_chess_rlt45, TileType::rookMoves, true),
    BLANK(-1, TileType::blankMoves, true),
    BLACK_BISHOP(R.drawable.ic_chess_bdt45, TileType::bishopMoves, false),
    BLACK_KING(R.drawable.ic_chess_kdt45, TileType::kingMoves, false),
    BLACK_KNIGHT(R.drawable.ic_chess_ndt45, TileType::knightMoves, false),
    BLACK_PAWN(R.drawable.ic_chess_pdt45, TileType::pawnMoves, false),
    BLACK_QUEEN(R.drawable.ic_chess_qdt45, TileType::queenMoves, false),
    BLACK_ROOK(R.drawable.ic_chess_rdt45, TileType::rookMoves, false);

    private static List<Position> blankMoves(int x, int y, IntersectionChecker checker) {
        return Collections.emptyList();
    }

    private static List<Position> pawnMoves(int x, int y, IntersectionChecker checker) {
        ArrayList<Position> alp = new ArrayList<>();
        if (!checker.check(x, y - 1))
            alp.add(new Position(x, y - 1));
        if (!checker.check(x, y - 2))
            alp.add(new Position(x, y - 2));
        if (!checker.check(x + 1, y - 1))
            alp.add(new Position(x + 1, y - 1));
        if (!checker.check(x - 1, y - 1))
            alp.add(new Position(x - 1, y - 1));
        return alp;
    }

    private static List<Position> bishopMoves(int x, int y, IntersectionChecker checker) {

        ArrayList<Position> alp = new ArrayList<>();
        boolean check = false;
        for (int i = y + 1, j = x + 1; i < 8 && j < 8 && !check; i++, j++){
            alp.add(new Position(j, i));
            check = checker.check(j, i);
        }
        check = false;
        for (int i = y + 1, j = x - 1; i < 8 && j >= 0 && !check; i++, j--){
            alp.add(new Position(j, i));
            check = checker.check(j, i);
        }
        check = false;
        for (int i = y - 1, j = x + 1; i >= 0 && j < 8 && !check; i--, j++){
            alp.add(new Position(j, i));
            check = checker.check(j, i);
        }
        check = false;
        for (int i = y - 1, j = x - 1; i >= 0 && j >= 0 && !check; i--, j--){
            alp.add(new Position(j, i));
            check = checker.check(j, i);
        }
        return alp;
    }

    private static List<Position> kingMoves(int x, int y, IntersectionChecker checker) {
        return Arrays.asList(
                new Position(x, y - 1),
                new Position(x, y + 1),
                new Position(x + 1, y),
                new Position(x - 1, y),
                new Position(x - 1, y - 1),
                new Position(x + 1, y + 1),
                new Position(x - 1, y + 1),
                new Position(x + 1, y - 1)
        );
    }

    private static List<Position> knightMoves(int x, int y, IntersectionChecker checker) {
        return Arrays.asList(
                new Position(x - 1, y - 2),
                new Position(x + 1, y - 2),
                new Position(x + 2, y + 1),
                new Position(x + 2, y - 1),
                new Position(x - 2, y - 1),
                new Position(x - 2, y + 1),
                new Position(x - 1, y + 2),
                new Position(x + 1, y + 2)
        );
    }

    private static List<Position> queenMoves(int x, int y, IntersectionChecker checker) {

        ArrayList<Position> alp = new ArrayList<>();
        boolean check = false;
        for (int i = y + 1, j = x + 1; i < 8 && j < 8 && !check; i++, j++){
            alp.add(new Position(j, i));
            check = checker.check(j, i);
        }
        check = false;
        for (int i = y + 1, j = x - 1; i < 8 && j >= 0 && !check; i++, j--){
            alp.add(new Position(j, i));
            check = checker.check(j, i);
        }
        check = false;
        for (int i = y - 1, j = x + 1; i >= 0 && j < 8 && !check; i--, j++){
            alp.add(new Position(j, i));
            check = checker.check(j, i);
        }
        check = false;
        for (int i = y - 1, j = x - 1; i >= 0 && j >= 0 && !check; i--, j--){
            alp.add(new Position(j, i));
            check = checker.check(j, i);
        }
        check = false;
        for (int i = y - 1; i >= 0 && !check; i--){
            alp.add(new Position(x, i));
            check = checker.check(x, i);
        }
        check = false;
        for (int i = y + 1; i < 8 && !check; i++){
            alp.add(new Position(x, i));
            check = checker.check(x, i);
        }
        check = false;
        for (int j = x - 1; j >= 0 && !check; j--){
            alp.add(new Position(j, y));
            check = checker.check(j, y);
        }
        check = false;
        for (int j = x + 1; j < 8 && !check; j++){
            alp.add(new Position(j, y));
            check = checker.check(j, y);
        }

        return alp;
    }

    private static List<Position> rookMoves(int x, int y, IntersectionChecker checker) {
        ArrayList<Position> alp = new ArrayList<>();
        for (int i = y - 1; i >= 0 && !checker.check(x, i); i--)
            alp.add(new Position(x, i));
        for (int i = y + 1; i < 8 && !checker.check(x, i); i++)
            alp.add(new Position(x, i));
        for (int j = x - 1; j >= 0 && !checker.check(j, y); j--)
            alp.add(new Position(j, y));
        for (int j = x + 1; j < 8 && !checker.check(j, y); j++)
            alp.add(new Position(j, y));
        return alp;
    }

    boolean pawnFirstMove = true;

    @DrawableRes
    private final int value;
    private final MovesCalculator movesCalculator;
    private final boolean isWhite;

    TileType(int value, MovesCalculator movesCalculator, boolean isWhite) {
        this.value = value;
        this.movesCalculator = movesCalculator;
        this.isWhite = isWhite;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public List<Position> getMovesFor(int x, int y, IntersectionChecker intersectionChecker) {
        return movesCalculator.calculateFor(x, y, intersectionChecker);
    }

    public int getValue() {
        return value;
    }
}
