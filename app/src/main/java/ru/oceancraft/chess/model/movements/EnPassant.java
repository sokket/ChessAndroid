package ru.oceancraft.chess.model.movements;

import java.util.Objects;

import ru.oceancraft.chess.model.Position;

public class EnPassant extends Movement {
    public Position deadPawn;
    public Position oldPosition;

    public EnPassant(Position deadPawn, Position oldPosition, Position newPosition) {
        this.deadPawn = deadPawn;
        this.oldPosition = oldPosition;
        this.highLighted = newPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EnPassant enPassant = (EnPassant) o;
        return deadPawn.equals(enPassant.deadPawn) &&
                oldPosition.equals(enPassant.oldPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), deadPawn, oldPosition);
    }
}
