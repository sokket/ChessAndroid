package ru.oceancraft.chess.net;

public class ChessClient {

    private EventListener eventListener;

    public ChessClient() {
        System.loadLibrary("native-lib");
    }

    public native boolean join(String key);

    public native String newRoom();

    public native boolean connect(String serverAddress, int port);

    public native void streamEvents(EventListener eventListener);

    public native void move(int xOld, int yOld, int xNew, int yNew);

    public native void sendMessage(String text);

    public native void enPassant(int x, int y);

    public native void castling(boolean longCastling);

    public native void disconnect();
}
