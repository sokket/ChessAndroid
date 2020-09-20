package com.example.chess.game;

import java.util.ArrayList;

public enum TileType {
    LIGHT_BISHOP(TileType::bishopMoves, true),
    LIGHT_KING(TileType::blankMoves, true),
    LIGHT_KNIGHT(TileType::knightMoves, true),
    LIGHT_PAWN(TileType::pawnMoves, true),
    LIGHT_QUEEN(TileType::blankMoves, true),
    LIGHT_ROOK(TileType::blankMoves, true),
    BLANK(TileType::blankMoves, true),
    BLACK_BISHOP(TileType::bishopMoves, false),
    BLACK_KING(TileType::blankMoves, false),
    BLACK_KNIGHT(TileType::knightMoves, false),
    BLACK_PAWN(TileType::pawnMoves, false),
    BLACK_QUEEN(TileType::blankMoves, false),
    BLACK_ROOK(TileType::blankMoves, false);

    private static Position[][] blankMoves(int x, int y) {
        return new Position[][]{};
    }

    private static Position[][] pawnMoves(int x, int y) {
        return new Position[][]{
                new Position[]{
                        new Position(x, y - 1),
                        new Position(x, y - 2)
                },
                new Position[]{
                        new Position(x + 1, y - 1)
                },
                new Position[]{
                        new Position(x - 1, y - 1)
                }
        };
    }


    private static Position[][] bishopMoves(int x, int y) {
        Position[][] matrix = new Position[4][];
        ArrayList<Position> tmp = new ArrayList<>();
        for (int i = y + 1, j = x + 1; i < 8 && j < 8; i++, j++)
            tmp.add(new Position(j, i));
        matrix[0] = new Position[0];
        matrix[0] = tmp.toArray(matrix[0]);
        tmp.clear();
        for (int i = y + 1, j = x - 1; i < 8 && j >= 0; i++, j--)
            tmp.add(new Position(j, i));
        matrix[1] = new Position[0];
        matrix[1] = tmp.toArray(matrix[1]);
        tmp.clear();
        for (int i = y - 1, j = x + 1; i >= 0 && j < 8; i--, j++)
            tmp.add(new Position(j, i));
        matrix[2] = new Position[0];
        matrix[2] = tmp.toArray(matrix[2]);
        tmp.clear();
        for (int i = y - 1, j = x - 1; i >= 0 && j >= 0; i--, j--)
            tmp.add(new Position(j, i));
        matrix[3] = new Position[0];
        matrix[3] = tmp.toArray(matrix[3]);
        tmp.clear();
        return matrix;
    }

    private static Position[][] knightMoves(int x, int y) {
        return new Position[][]{
                new Position[]{new Position(x - 1, y - 2)},
                new Position[]{new Position(x + 1, y - 2)},
                new Position[]{new Position(x + 2, y + 1)},
                new Position[]{new Position(x + 2, y - 1)},
                new Position[]{new Position(x - 2, y - 1)},
                new Position[]{new Position(x - 2, y + 1)},
                new Position[]{new Position(x - 1, y + 2)},
                new Position[]{new Position(x + 1, y + 2)},
        };
    }

    /*private static Position[][] kingMoves(int x, int y) {
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



    private static Position[][] queenMoves(int x, int y) {
        ArrayPosition[][] alp = new ArrayList<>();
        for (int i = y + 1, j = x + 1; i < 8 && j < 8; i++, j++)
            alp.add(new Position(j, i));
        for (int i = y + 1, j = x - 1; i < 8 && j >= 0; i++, j--)
            alp.add(new Position(j, i));
        for (int i = y - 1, j = x + 1; i >= 0 && j < 8; i--, j++)
            alp.add(new Position(j, i));
        for (int i = y - 1, j = x - 1; i >= 0 && j >= 0; i--, j--)
            alp.add(new Position(j, i));
        for (int i = y - 1; i >= 0; i--)
            alp.add(new Position(x, i));
        for (int i = y + 1; i < 8; i++)
            alp.add(new Position(x, i));
        for (int j = x - 1; j >= 0; j--)
            alp.add(new Position(j, y));
        for (int j = x + 1; j < 8; j++)
            alp.add(new Position(j, y));
        return alp;
    }

    private static Position[][] rookMoves(int x, int y) {
        ArrayPosition[][] alp = new ArrayList<>();
        for (int i = y - 1; i >= 0; i--)
            alp.add(new Position(x, i));
        for (int i = y + 1; i < 8; i++)
            alp.add(new Position(x, i));
        for (int j = x - 1; j >= 0; j--)
            alp.add(new Position(j, y));
        for (int j = x + 1; j < 8; j++)
            alp.add(new Position(j, y));
        return alp;
    }
*/
    boolean pawnFirstMove = true;

    private final MovesCalculator movesCalculator;
    private final boolean isWhite;

    TileType(MovesCalculator movesCalculator, boolean isWhite) {
        this.movesCalculator = movesCalculator;
        this.isWhite = isWhite;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public Position[][] getMovesFor(int x, int y) {
        return movesCalculator.calculateFor(x, y);
    }
}
