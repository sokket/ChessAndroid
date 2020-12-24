package ru.oceancraft.chess.model;

public interface ActionTransmitter {
    void setOnMakeMoveListener(MoveListener eventListener);

    void makeMove(int xOld, int yOld, int xNew, int yNew);

    void enPassant(int x, int y);

    void castling(boolean longCastling);

    void setOnEnPassantListener(EnPassantListener enPassantListener);

    void setOnCastlingListener(CastlingListener castlingListener);
}
