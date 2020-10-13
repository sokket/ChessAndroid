package com.example.chess.game;

import java.util.Objects;

public class SimpleMovement extends Movement {
    public Position oldPosition;

    public SimpleMovement(Position oldPosition, Position newPosition) {
        this.oldPosition = oldPosition;
        this.highLighted = newPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SimpleMovement that = (SimpleMovement) o;
        return oldPosition.equals(that.oldPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), oldPosition);
    }
}
