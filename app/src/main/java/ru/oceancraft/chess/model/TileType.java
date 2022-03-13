package ru.oceancraft.chess.model;

import java.util.ArrayList;
import java.util.List;

public enum TileType {
    WHITE_BISHOP(TileType::bishopMoves, true, 'B'),
    WHITE_KING(TileType::kingMoves, true, 'K'),
    WHITE_KNIGHT(TileType::knightMoves, true, 'N'),
    WHITE_PAWN(TileType::whitePawnMoves, true, ' '),
    WHITE_QUEEN(TileType::queenMoves, true, 'Q'),
    WHITE_ROOK(TileType::rookMoves, true, 'R'),
    BLANK(TileType::blankMoves, true, 'b'),
    BLACK_BISHOP(TileType::bishopMoves, false, 'B'),
    BLACK_KING(TileType::kingMoves, false, 'K'),
    BLACK_KNIGHT(TileType::knightMoves, false, 'N'),
    BLACK_PAWN(TileType::blackPawnMoves, false, ' '),
    BLACK_QUEEN(TileType::queenMoves, false, 'Q'),
    BLACK_ROOK(TileType::rookMoves, false, 'R');

    private static Position[][] blankMoves(int x, int y) {
        return new Position[][]{};
    }

    private static Position[][] whitePawnMoves(int x, int y) {
        return new Position[][]{
                y == 6 ? new Position[]{
                        new Position(x, y - 1),
                        new Position(x, y - 2)
                } : new Position[]{
                        new Position(x, y - 1)
                }, new Position[]{
                new Position(x - 1, y - 1)
        }, new Position[]{
                new Position(x + 1, y - 1)
        }
        };
    }

    private static Position[][] blackPawnMoves(int x, int y) {
        return new Position[][]{
                y == 1 ? new Position[]{
                        new Position(x, y + 1),
                        new Position(x, y + 2)
                } : new Position[]{
                        new Position(x, y + 1)
                }, new Position[]{
                new Position(x - 1, y + 1)
        }, new Position[]{
                new Position(x + 1, y + 1)
        }
        };
    }


    private static Position[][] bishopMoves(int x, int y) {
        Position[][] matrix = new Position[4][];
        for (int i = 0; i < 4; i++) matrix[i] = new Position[0];
        matrix[0] = mainDiagonal(x, y, true).toArray(matrix[0]);
        matrix[1] = sideDiagonal(x, y, true).toArray(matrix[1]);
        matrix[2] = sideDiagonal(x, y, false).toArray(matrix[2]);
        matrix[3] = mainDiagonal(x, y, false).toArray(matrix[3]);
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

    private static Position[][] kingMoves(int x, int y) {
        return new Position[][]{
                new Position[]{new Position(x, y - 1)},
                new Position[]{new Position(x, y + 1)},
                new Position[]{new Position(x + 1, y - 1)},
                new Position[]{new Position(x - 1, y - 1)},
                new Position[]{new Position(x + 1, y + 1)},
                new Position[]{new Position(x - 1, y + 1)},
                new Position[]{new Position(x + 1, y)},
                new Position[]{new Position(x - 1, y)},
                y == 0 || y == 7 ?
                        new Position[]{
                                new Position(x + 1, y),
                                new Position(x + 2, y)
                        } : new Position[]{new Position(-1, -1)},
                y == 0 || y == 7 ?
                        new Position[]{
                                new Position(x - 1, y),
                                new Position(x - 2, y),
                                new Position(x - 3, y),
                                new Position(x - 4, y)
                        } : new Position[]{new Position(-1, -1)}
        };
    }

    private static Position[][] queenMoves(int x, int y) {
        Position[][] matrix = new Position[8][];
        for (int i = 0; i < 8; i++) matrix[i] = new Position[0];
        matrix[0] = axisX(x, y, true).toArray(matrix[0]);
        matrix[1] = axisY(x, y, true).toArray(matrix[1]);
        matrix[2] = axisY(x, y, false).toArray(matrix[2]);
        matrix[3] = axisX(x, y, false).toArray(matrix[3]);
        matrix[4] = mainDiagonal(x, y, false).toArray(matrix[4]);
        matrix[5] = sideDiagonal(x, y, false).toArray(matrix[5]);
        matrix[6] = sideDiagonal(x, y, true).toArray(matrix[6]);
        matrix[7] = mainDiagonal(x, y, true).toArray(matrix[7]);
        return matrix;
    }

    private static Position[][] rookMoves(int x, int y) {
        Position[][] matrix = new Position[4][];
        for (int i = 0; i < 4; i++) matrix[i] = new Position[0];
        matrix[0] = axisX(x, y, true).toArray(matrix[0]);
        matrix[1] = axisY(x, y, true).toArray(matrix[1]);
        matrix[2] = axisY(x, y, false).toArray(matrix[2]);
        matrix[3] = axisX(x, y, false).toArray(matrix[3]);
        return matrix;
    }


    private static List<Position> mainDiagonal(int x, int y, boolean onRight) {
        ArrayList<Position> tmp = new ArrayList<>();
        if (onRight)
            for (int i = y + 1, j = x + 1; i < 8 && j < 8; i++, j++)
                tmp.add(new Position(j, i));
        else
            for (int i = y - 1, j = x - 1; i >= 0 && j >= 0; i--, j--)
                tmp.add(new Position(j, i));
        return tmp;
    }

    private static List<Position> sideDiagonal(int x, int y, boolean onRight) {
        ArrayList<Position> tmp = new ArrayList<>();
        if (onRight)
            for (int i = y - 1, j = x + 1; i >= 0 && j < 8; i--, j++)
                tmp.add(new Position(j, i));
        else
            for (int i = y + 1, j = x - 1; i < 8 && j >= 0; i++, j--)
                tmp.add(new Position(j, i));
        return tmp;
    }

    private static List<Position> axisX(int x, int y, boolean onRight) {
        ArrayList<Position> tmp = new ArrayList<>();
        if (onRight) {
            for (int j = x + 1; j < 8; j++)
                tmp.add(new Position(j, y));
        } else {
            for (int j = x - 1; j >= 0; j--)
                tmp.add(new Position(j, y));
        }
        return tmp;
    }

    private static List<Position> axisY(int x, int y, boolean onRight) {
        ArrayList<Position> tmp = new ArrayList<>();
        if (onRight) {
            for (int i = y + 1; i < 8; i++)
                tmp.add(new Position(x, i));
        } else {
            for (int i = y - 1; i >= 0; i--)
                tmp.add(new Position(x, i));
        }
        return tmp;
    }

    boolean pawnFirstMove = true;

    private final MovesCalculator movesCalculator;
    private final boolean isWhite;
    private final char name;

    public char getName() {
        return name;
    }

    TileType(MovesCalculator movesCalculator, boolean isWhite, char name) {
        this.movesCalculator = movesCalculator;
        this.isWhite = isWhite;
        this.name = name;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public boolean isPawnFirstMove() {
        return pawnFirstMove;
    }

    public void setPawnFirstMove(boolean pawnFirstMove) {
        this.pawnFirstMove = pawnFirstMove;
    }

    public Position[][] getMovesFor(int x, int y) {
        return movesCalculator.calculateFor(x, y);
    }
}
