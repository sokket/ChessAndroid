package ru.oceancraft.chess.model.listeners;

import ru.oceancraft.chess.model.TileType;

public interface OnPromotionListener {
    void onSelectTileType(TileType newTileTye);
}
