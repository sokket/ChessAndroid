package com.example.chess.game;

public interface ActionTransmitter {
    void setOnMakeMoveListener(MoveListener moveListener);

    void makeMove(int xOld, int yOld, int xNew, int yNew);

    void enPassant(Position deadPawn);

    void castling(boolean longCastling);

    void setOnEnPassantListener(EnPassantListener enPassantListener);

    void setOnCastlingListener(CastlingListener castlingListener);
}
