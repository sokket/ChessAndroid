package ru.oceancraft.chess.model.movements;

import java.util.Objects;

import ru.oceancraft.chess.model.Position;

public abstract class Movement {
    public Position highLighted;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movement movement = (Movement) o;
        return highLighted.equals(movement.highLighted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(highLighted);
    }
}
