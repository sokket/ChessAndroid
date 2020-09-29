package com.example.chess.game;

import java.util.ArrayList;
import java.util.List;

public class ChessGame {
    private final Tile[][] gameBoard = new Tile[8][8];
    private final ChessView chessView;
    private ActionTransmitter actionTransmitter;
    private final boolean netMode;

    private int highLightSrcX;
    private int highLightSrcY;

    private boolean whiteTurn = true;

    public ChessGame(ChessView chessView, ActionTransmitter actionTransmitter) {
        this.chessView = chessView;
        this.actionTransmitter = actionTransmitter;
        netMode = true;
    }

    public ChessGame(ChessView chessView) {
        this.chessView = chessView;
        netMode = false;
    }

    private List<Position> trimRays(boolean isSrcWhite, Position[][] moves) {
        ArrayList<Position> trimmed = new ArrayList<>();
        TileType tileType = gameBoard[highLightSrcY][highLightSrcX].getTileType();
        boolean isPawn = tileType == TileType.LIGHT_PAWN || tileType == TileType.BLACK_PAWN;

        for (int i = 0; i < moves.length; i++)
            for (Position position : moves[i])
                if (checkOverLap(position)) {
                    TileType targetTileType = getTileType(position);
                    boolean targetColor = isBlack(targetTileType);

                    if (targetTileType == TileType.BLANK && (!isPawn || i == 0)) {
                        trimmed.add(position);
                    } else if (
                            targetTileType == TileType.BLACK_KING ||
                                    targetTileType == TileType.LIGHT_KING) {
                        break;
                    } else if (
                            targetTileType != TileType.BLANK &&
                                    isSrcWhite != targetColor && (!isPawn || i != 0)) {
                        trimmed.add(position);
                        break;
                    } else
                        break;
                }

        return trimmed;
    }

    private boolean isBlack(TileType tileType) {
        return tileType.isWhite();
    }

    private TileType getTileType(Position position) {
        return gameBoard[position.getY()][position.getX()].getTileType();
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
                if (netMode)
                    actionTransmitter.makeMove(highLightSrcX, highLightSrcY, x, y);
                chessView.onNewLogLine(new LogLine(highLightSrcX, highLightSrcY, x, y, targetTileType.getName()));
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
        chessView.setResetOnPressListener(this::reset);

        if (netMode)
            actionTransmitter.setOnMakeMoveListener((oX, oY, nX, nY) -> {
                if (checkOverLap(new Position(oX, oY)) && checkOverLap(new Position(nX, nY))) {
                    TileType tileType = gameBoard[oY][oX].getTileType();
                    gameBoard[oY][oX].setTileType(TileType.BLANK);
                    gameBoard[nY][nX].setTileType(tileType);
                    chessView.onNewLogLine(new LogLine(oX, oY, nX, nY, tileType.getName()));
                }
            });
    }

    TileType startingLineup(int i, int j) {
        if (i == 1) return TileType.BLACK_PAWN;
        else if (i == 6) return TileType.LIGHT_PAWN;
        else if (i == 0 && (j == 0 || j == 7)) return TileType.BLACK_ROOK;
        else if (i == 7 && (j == 0 || j == 7)) return TileType.LIGHT_ROOK;
        else if (i == 0 && (j == 1 || j == 6)) return TileType.BLACK_KNIGHT;
        else if (i == 7 && (j == 1 || j == 6)) return TileType.LIGHT_KNIGHT;
        else if (i == 0 && (j == 2 || j == 5)) return TileType.BLACK_BISHOP;
        else if (i == 7 && (j == 2 || j == 5)) return TileType.LIGHT_BISHOP;
        else if (i == 0 && j == 3) return TileType.BLACK_QUEEN;
        else if (i == 7 && j == 3) return TileType.LIGHT_QUEEN;
        else if (i == 0 && j == 4) return TileType.BLACK_KING;
        else if (i == 7 && j == 4) return TileType.LIGHT_KING;
        return TileType.BLANK;
    }

    void reset() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                gameBoard[i][j].setTileType(startingLineup(i, j));
                gameBoard[i][j].setHighLighted(false);
            }
        chessView.cleanLog();
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
