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
    private boolean enPassAnt = false;

    private Position posForPassAnt = null;
    private int check = 0;
    private int checkmate = 1;

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
        enPassAnt = false;

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
                        if (isSrcWhite != targetColor) {
                            check++;
                            System.out.println("CHECK");
                        }
                        break;
                    } else if (isPawn && i != 0 && posForPassAnt != null &&
                            Math.abs(position.getY() - posForPassAnt.getY()) == 1 &&
                            position.getX() == posForPassAnt.getX() &&
                            isSrcWhite != getTileType(posForPassAnt).isWhite()
                    ) {
                        trimmed.add(position);
                        posForPassAnt = null;
                        enPassAnt = true;
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

    /*private List<Position> trimRaysForDelCheck () {

    }*/

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
            if (currentTile.isLighted() && (!netMode || whiteGame == whiteTurn) && !(checkmate == 2)) {
                clearHighLight();
                Tile highLightSourceTile = gameBoard[highLightSrcY][highLightSrcX];
                TileType targetTileType = highLightSourceTile.getTileType();
                highLightSourceTile.setTileType(TileType.BLANK);

                if (Math.abs(highLightSrcY - y) == 2 &&
                        (targetTileType == TileType.BLACK_PAWN ||
                                targetTileType == TileType.LIGHT_PAWN))
                    posForPassAnt = new Position(x, y);
                else posForPassAnt = null;

                if (enPassAnt)
                    if (y == 5)
                        gameBoard[y - 1][x].setTileType(TileType.BLANK);
                    else
                        gameBoard[y + 1][x].setTileType(TileType.BLANK);
                enPassAnt = false;

                currentTile.setTileType(targetTileType);
                checkForCheck();
                if (checkmate > 1) System.out.println("CHECKMATE");

                if (netMode) {
                    actionTransmitter.makeMove(highLightSrcX, highLightSrcY, x, y);
                    if (enPassAnt)
                        if (y == 5)
                            actionTransmitter.makeMove(highLightSrcX, highLightSrcY, x, y - 1);
                        else
                            actionTransmitter.makeMove(highLightSrcX, highLightSrcY, x, y + 1);
                } else {
                    chessView.onMoveFinished(!whiteTurn);
                    syncWithView();
                }

                chessView.onNewLogLine(new LogLine(highLightSrcX, highLightSrcY, x, y, targetTileType.getName(), check != 0, checkmate == 2));
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

                    if (Math.abs(oY - nY) == 2 &&
                            (tileType == TileType.BLACK_PAWN ||
                                    tileType == TileType.LIGHT_PAWN))
                        posForPassAnt = new Position(nX, nY);
                    else posForPassAnt = null;

                    gameBoard[oY][oX].setTileType(TileType.BLANK);
                    gameBoard[nY][nX].setTileType(tileType);
                    checkForCheck();
                    if (checkmate > 1) System.out.println("CHECKMATE");
                    chessView.onNewLogLine(new LogLine(oX, oY, nX, nY, tileType.getName(), check != 0, checkmate == 2));
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

    void checkForCheck() {
        check = 0;
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                TileType currentTileType = gameBoard[j][i].getTileType();
                Position[][] moves = currentTileType.getMovesFor(i, j);
                trimRays(currentTileType.isWhite(), moves);
            }
        checkmate += check != 0 ? 1 : checkmate * -1;
        System.out.println(checkmate);
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
        checkmate = 0;
        check = 0;
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
