package com.example.chess.net;

import com.example.chess.game.MoveListener;

public class ChessClient {

    private MoveListener moveListener;

    public ChessClient() {
        System.loadLibrary("native-lib");
    }

    public native boolean join(String key);

    public native String newRoom();

    public native boolean connect(String serverAddress, int port);

    public native void streamMoves(MoveListener moveListener);

    public native void move(int xOld, int yOld, int xNew, int yNew);

}
