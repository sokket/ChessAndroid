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

    private boolean whiteGame;
    private boolean whiteTurn = true;

    public ChessGame(ChessView chessView, ActionTransmitter actionTransmitter, boolean whiteGame) {
        this.chessView = chessView;
        this.actionTransmitter = actionTransmitter;
        this.whiteGame = whiteGame;
        this.netMode = true;
    }

    public ChessGame(ChessView chessView) {
        this.chessView = chessView;
        this.whiteGame = true;
        this.netMode = false;
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

    private void syncWithView() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                chessView.onChangeTile(i, j, gameBoard[j][i].getTileType());
                gameBoard[i][j].setHighLighted(false);
            }
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
            if (currentTile.isLighted() && (!netMode || whiteGame == whiteTurn)) {
                clearHighLight();
                Tile highLightSourceTile = gameBoard[highLightSrcY][highLightSrcX];
                TileType targetTileType = highLightSourceTile.getTileType();
                highLightSourceTile.setTileType(TileType.BLANK);
                currentTile.setTileType(targetTileType);

                if (netMode)
                    actionTransmitter.makeMove(highLightSrcX, highLightSrcY, x, y);
                else {
                    chessView.onMoveFinished(!whiteTurn);
                    syncWithView();
                }

                chessView.onNewLogLine(new LogLine(highLightSrcX, highLightSrcY, x, y, targetTileType.getName()));
                whiteTurn = !whiteTurn;
            } else {
                clearHighLight();
                highLightSrcX = x;
                highLightSrcY = y;

                drawHighLight(x, y);
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

                    clearHighLight();
                    drawHighLight(highLightSrcX, highLightSrcY);

                    whiteTurn = !whiteTurn;
                }
            });
    }

    void drawHighLight(int x, int y) {
        TileType currentTileType = gameBoard[y][x].getTileType();
        if (netMode ? currentTileType.isWhite() == whiteGame : currentTileType.isWhite() == whiteTurn) {
            Position[][] moves = currentTileType.getMovesFor(x, y);
            trimRays(currentTileType.isWhite(), moves).forEach(it ->
                    gameBoard[it.getY()][it.getX()].setHighLighted(true)
            );
        }
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
        whiteTurn = true;
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
