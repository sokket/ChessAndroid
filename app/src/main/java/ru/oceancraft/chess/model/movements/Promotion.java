package ru.oceancraft.chess.model.movements;

import java.util.Objects;

import ru.oceancraft.chess.model.Position;
import ru.oceancraft.chess.model.TileType;

public class Promotion extends Movement {

    public TileType newTileType;
    public Movement movement;

    public Promotion(Movement movement, TileType newTileType) {
        this.highLighted = movement.highLighted;
        this.movement = movement;
        this.newTileType = newTileType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Promotion promotion = (Promotion) o;
        return newTileType == promotion.newTileType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), newTileType);
    }
}
