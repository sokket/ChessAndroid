package ru.oceancraft.chess.model;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private final boolean netMode;
    private final List<String> board;

    private final boolean isWhiteMove;
    private final boolean isWhiteGame;
    private final boolean checkmate;
    private final boolean stalemate;

    private final boolean allowedCastlingForLWR;
    private final boolean allowedCastlingForLBR;
    private final boolean allowedCastlingForRWR;
    private final boolean allowedCastlingForRBR;

    private final Position lastPawnMove;

    public GameState(
            Tile[][] board,
            boolean netMode, boolean isWhiteMove,
            boolean isWhiteGame,
            boolean checkmate, boolean stalemate,
            boolean allowedCastlingForLWR,
            boolean allowedCastlingForLBR,
            boolean allowedCastlingForRWR,
            boolean allowedCastlingForRBR,
            Position lastPawnMove) {
        this.netMode = netMode;
        this.checkmate = checkmate;
        this.stalemate = stalemate;
        this.allowedCastlingForLWR = allowedCastlingForLWR;
        this.allowedCastlingForLBR = allowedCastlingForLBR;
        this.allowedCastlingForRWR = allowedCastlingForRWR;
        this.allowedCastlingForRBR = allowedCastlingForRBR;
        this.isWhiteMove = isWhiteMove;
        this.isWhiteGame = isWhiteGame;
        this.lastPawnMove = lastPawnMove;

        this.board = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                this.board.add(board[i][j].getTileType().name());
            }
        }
    }

    public TileType[][] getBoard() {
        TileType[][] gameBoard = new TileType[8][8];
        int f = 0;
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                gameBoard[i][j] = TileType.valueOf(board.get(f++));
            }
        }
        return gameBoard;
    }

    public boolean isWhiteMove() {
        return isWhiteMove;
    }

    public boolean isWhiteGame() {
        return isWhiteGame;
    }

    public boolean isAllowedCastlingForRBR() {
        return allowedCastlingForRBR;
    }

    public boolean isAllowedCastlingForRWR() {
        return allowedCastlingForRWR;
    }

    public boolean isAllowedCastlingForLBR() {
        return allowedCastlingForLBR;
    }

    public boolean isAllowedCastlingForLWR() {
        return allowedCastlingForLWR;
    }

    public Position getLastPawnMove() {
        return lastPawnMove;
    }

    public boolean isNetMode() {
        return netMode;
    }

    public boolean isCheckmate() {
        return checkmate;
    }

    public boolean isStalemate() {
        return stalemate;
    }
}
