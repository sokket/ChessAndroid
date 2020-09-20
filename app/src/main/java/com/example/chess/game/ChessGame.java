package com.example.chess.game;

import java.util.ArrayList;
import java.util.List;

public class ChessGame {
    private final Tile[][] gameBoard = new Tile[8][8];
    private final ChessView chessView;

    private int highLightSrcX;
    private int highLightSrcY;

    private boolean whiteTurn = true;

    public ChessGame(ChessView chessView) {
        this.chessView = chessView;
    }

    private List<Position> trimRays(boolean isSrcWhite, Position[][] moves) {
        ArrayList<Position> trimmed = new ArrayList<>();
        for (Position[] positions : moves) {
            boolean cut = false;
            for (Position position : positions)
                if (!cut && checkOverLap(position)) {
                    Tile tile = gameBoard[position.getY()][position.getX()];
                    boolean targetColor = tile.getTileType().isWhite();
                    if (tile.getTileType() == TileType.BLANK)
                        trimmed.add(position);
                    else if (
                            tile.getTileType() == TileType.BLACK_KING ||
                                    tile.getTileType() == TileType.LIGHT_KING)
                        cut = true;
                    else if (isSrcWhite != targetColor) {
                        trimmed.add(position);
                        cut = true;
                    } else
                        cut = true;
                }
        }
        return trimmed;
    }

    public void initGame() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                final boolean isBlack =
                        (i % 2 == 0 && j % 2 != 0) || (i % 2 != 0 && j % 2 == 0);
                final TileType initFigure = startingLineup(i, j);

                int finalY = i;
                int finalX = j;

                gameBoard[finalY][finalX] = new Tile(
                        isBlack,
                        initFigure,
                        isHighLighted -> chessView.onHighLight(finalX, finalY, isHighLighted, isBlack),
                        tileType -> chessView.onChangeTile(finalX, finalY, tileType)
                );
            }

        chessView.setOnPressListener((x, y) -> {
            Tile currentTile = gameBoard[y][x];
            if (currentTile.isLighted()) {
                clearHighLight();
                Tile highLightSourceTile = gameBoard[highLightSrcY][highLightSrcX];
                TileType targetTileType = highLightSourceTile.getTileType();
                highLightSourceTile.setTileType(TileType.BLANK);
                currentTile.setTileType(targetTileType);
            } else {
                clearHighLight();
                highLightSrcX = x;
                highLightSrcY = y;

                TileType currentTileType = currentTile.getTileType();
                Position[][] moves = currentTileType.getMovesFor(x, y);
                trimRays(currentTileType.isWhite(), moves).forEach(it ->
                        gameBoard[it.getY()][it.getX()].setHighLighted(true)
                );
            }
        });
    }

    TileType startingLineup(int i, int j) {
        if (i == 1) return TileType.BLACK_PAWN;
        else if (i == 6) return TileType.LIGHT_BISHOP;
        else if (i == 0 && (j == 0 || j == 7)) return TileType.BLACK_ROOK;
        else if (i == 7 && (j == 0 || j == 7)) return TileType.LIGHT_ROOK;
        else if (i == 0 && (j == 1 || j == 6)) return TileType.BLACK_KNIGHT;
        else if (i == 7 && (j == 1 || j == 6)) return TileType.LIGHT_KNIGHT;
        else if (i == 0 && (j == 2 || j == 5)) return TileType.BLACK_BISHOP;
        else if (i == 7 && (j == 2 || j == 5)) return TileType.LIGHT_BISHOP;
        else if (i == 0 && j == 3) return TileType.BLACK_QUEEN;
        else if (i == 7 && j == 4) return TileType.LIGHT_QUEEN;
        else if (i == 0 && j == 4) return TileType.BLACK_KING;
        else if (i == 7 && j == 3) return TileType.LIGHT_KING;
        else if (i == 5 && j == 6) return TileType.LIGHT_QUEEN;
        else if (i == 3 && j == 4) return TileType.BLACK_PAWN;
        return TileType.BLANK;
    }

    void reset() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                gameBoard[i][j].setTileType(startingLineup(i, j));
                gameBoard[i][j].setHighLighted(false);
            }
    }

    void clearHighLight() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                gameBoard[i][j].setHighLighted(false);
    }

    private boolean checkOverLap(Position position) {
        int x = position.getX();
        int y = position.getY();
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

}
