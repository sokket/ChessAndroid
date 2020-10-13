package com.example.chess.game;

import java.util.Objects;

public class EatMovement extends Movement {
    public Position attackerPosition;

    public EatMovement(Position attackerPosition, Position targetPosition) {
        this.attackerPosition = attackerPosition;
        this.highLighted = targetPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EatMovement that = (EatMovement) o;
        return attackerPosition.equals(that.attackerPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), attackerPosition);
    }
}
