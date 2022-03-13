package ru.oceancraft.chess.model.movements;

import java.util.Objects;

import ru.oceancraft.chess.model.Position;

public class Castling extends Movement {
    public Position kingOldPosition;
    public Position rookOldPosition;
    public Position rookNewPosition;
    public boolean longCastling;

    public Castling(Position kingOldPosition,
                    Position kingNewPosition,
                    Position rookOldPosition,
                    Position rookNewPosition,
                    boolean longCastling) {
        this.kingOldPosition = kingOldPosition;
        this.highLighted = kingNewPosition;
        this.rookOldPosition = rookOldPosition;
        this.rookNewPosition = rookNewPosition;
        this.longCastling = longCastling;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Castling castling = (Castling) o;
        return kingOldPosition.equals(castling.kingOldPosition) &&
                rookOldPosition.equals(castling.rookOldPosition) &&
                rookNewPosition.equals(castling.rookNewPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), kingOldPosition, rookOldPosition, rookNewPosition);
    }
}
