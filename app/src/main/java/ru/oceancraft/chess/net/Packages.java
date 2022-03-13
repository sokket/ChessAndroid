package ru.oceancraft.chess.net;

import java.lang.reflect.Array;
import java.util.Arrays;

public enum Packages {
    CREATE_ROOM(0, 0),
    JOIN_ROOM(1, 0),
    MOVE(2, 4),
    CASTLING(3, 2),
    EN_PASSANT(4, 2),
    PROMOTION(5, 5),
    CHAT_MSG(100, -1),
    JOINED(6, -1),
    PING(7, 0),
    PONG(8, 0);

    public final byte num;
    public final int len;

    public static Packages valueOf(byte b) {
        return Arrays.stream(values()).filter(it -> it.num == b).findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    Packages(int num, int len) {
        this.num = (byte) num;
        this.len = len;
    }
}
