package ru.oceancraft.chess.model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Message {
    private final String text;
    private final boolean our;

    public Message(String text, boolean our) {
        this.text = text;
        this.our = our;
    }

    public String getText() {
        return text;
    }

    public boolean isOur() {
        return our;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return our == message.our &&
                text.equals(message.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, our);
    }

    @NotNull
    @Override
    public String toString() {
        return "Message{" +
                "text='" + text + '\'' +
                ", our=" + our +
                '}';
    }
}
