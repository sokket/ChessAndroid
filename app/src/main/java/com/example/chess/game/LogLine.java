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
    private boolean check;
    private boolean checkmate;

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

    public LogLine(int xOld, int yOld, boolean castling, boolean longCastling) {
        this.castling = castling;
        this.longCastling = longCastling;
    }

    public LogLine(int xOld, int yOld, int xNew, int yNew, char name, boolean check, boolean checkmate, boolean castling, boolean longCastling) {
        this.xOld = xOld;
        this.yOld = yOld;
        this.xNew = xNew;
        this.yNew = yNew;
        this.name = name;
        this.check = check;
        this.checkmate = checkmate;
        this.castling = castling;
        this.longCastling = longCastling;
    }


    @Override
    public String toString() {
        return castling ? (longCastling ? "0-0-0" : "0-0") :
                checkmate ?
                        (name == ' ' ?
                                String.format(Locale.ENGLISH, "%c%d-%c%d#", xOld + 97, 8 - yOld, xNew + 97, 8 - yNew) :
                                String.format(Locale.ENGLISH, "%c%c%d-%c%d#", name, xOld + 97, 8 - yOld, xNew + 97, 8 - yNew)) :
                        check ?
                                (name == ' ' ?
                                        String.format(Locale.ENGLISH, "%c%d-%c%d+", xOld + 97, 8 - yOld, xNew + 97, 8 - yNew) :
                                        String.format(Locale.ENGLISH, "%c%c%d-%c%d+", name, xOld + 97, 8 - yOld, xNew + 97, 8 - yNew)) :
                                (name == ' ' ?
                                        String.format(Locale.ENGLISH, "%c%d-%c%d", xOld + 97, 8 - yOld, xNew + 97, 8 - yNew) :
                                        String.format(Locale.ENGLISH, "%c%c%d-%c%d", name, xOld + 97, 8 - yOld, xNew + 97, 8 - yNew));
    }
}
