package ru.oceancraft.chess.model;

import androidx.annotation.NonNull;

public class LogLine {
    private final int xOld;
    private final int yOld;
    private final int xNew;
    private final int yNew;
    private final char name;
    private final boolean wasCapture;
    private final boolean castling;
    private final boolean longCastling;
    private final boolean check;
    private final boolean checkmate;
    private final boolean stalemate;

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

    public LogLine(int xOld,
                   int yOld,
                   int xNew,
                   int yNew,
                   char name,
                   boolean wasCapture,
                   boolean check,
                   boolean checkmate,
                   boolean castling,
                   boolean longCastling,
                   boolean stalemate) {
        this.xOld = xOld;
        this.yOld = yOld;
        this.xNew = xNew;
        this.yNew = yNew;
        this.name = name;
        this.wasCapture = wasCapture;
        this.check = check;
        this.checkmate = checkmate;
        this.castling = castling;
        this.longCastling = longCastling;
        this.stalemate = stalemate;
    }


    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (stalemate) {
            sb.append("1/2-1/2");
            return sb.toString();
        }

        if (castling) {
            sb.append(longCastling ? "0-0-0" : "0-0");
            return sb.toString();
        }

        if (name != ' ') sb.append(name);

        sb.append((char) ('a' + xOld));
        sb.append(8 - yOld);
        sb.append(wasCapture ? 'x' : '-');
        sb.append((char) ('a' + xNew));
        sb.append(8 - yNew);

        if (checkmate) sb.append('#');
        else if (check) sb.append('+');

        return sb.toString();
    }
}
