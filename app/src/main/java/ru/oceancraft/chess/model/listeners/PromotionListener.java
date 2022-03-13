package ru.oceancraft.chess.model.listeners;

public interface PromotionListener {
    void onPromotion(int oX, int oY, int nX, int nY, int nTT);
}
