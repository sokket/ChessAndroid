package ru.oceancraft.chess.model.action_transmitter;

import ru.oceancraft.chess.model.listeners.CastlingListener;
import ru.oceancraft.chess.model.listeners.EnPassantListener;
import ru.oceancraft.chess.model.listeners.MoveListener;
import ru.oceancraft.chess.model.listeners.PromotionListener;

public interface ActionTransmitter {
    void setOnMakeMoveListener(MoveListener eventListener);

     void makeMove(int xOld, int yOld, int xNew, int yNew);

     void setOnEnPassantListener(EnPassantListener enPassantListener);

     void enPassant(int x, int y);

     void setOnCastlingListener(CastlingListener castlingListener);

     void castling(boolean longCastling);

     void setOnPromotionListener(PromotionListener promotionListener);
     
     void promotion(int xOld, int yOld, int xNew, int yNew, char newTileType);
}
