package ru.oceancraft.chess.net;

@FunctionalInterface
public interface StatusCheckListener {
    void onCheckFinished();
}
