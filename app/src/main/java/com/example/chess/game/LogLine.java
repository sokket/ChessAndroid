package com.example.chess.game;

import java.util.Locale;

public class LogLine {
    private int xOld;
    private int yOld;
    private int xNew;
    private int yNew;
    private char name;
    private boolean castling;
    private boolean longCastling;

    public int getXOld() {
        return xOld;
    }

    public int getYOld() {
        return yOld;
    }

    public int getXNew() {
        return xNew;
    }

    public int getYNew() {
        return yNew;
    }

    public char getName() {
        return name;
    }

    public boolean isCastling() {
        return castling;
    }

    public LogLine(boolean castling, boolean longCastling) {
        this.castling = castling;
        this.longCastling = longCastling;
    }

    public LogLine(int xOld, int yOld, int xNew, int yNew, char name) {
        this.xOld = xOld;
        this.yOld = yOld;
        this.xNew = xNew;
        this.yNew = yNew;
        this.name = name;
    }

    @Override
    public String toString() {
        return castling ? (longCastling ? "0-0-0" : "0-0") :
                name == ' ' ? String.format(Locale.ENGLISH, "%c%d-%c%d", xOld + 97, 8 - yOld, xNew + 97, 8 - yNew) :
                        String.format(Locale.ENGLISH, "%c%c%d-%c%d", name, xOld + 97, 8 - yOld, xNew + 97, 8 - yNew);
    }
}
