package ru.oceancraft.chess.net;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.oceancraft.chess.model.ActionTransmitter;
import ru.oceancraft.chess.model.CastlingListener;
import ru.oceancraft.chess.model.EnPassantListener;
import ru.oceancraft.chess.model.MoveListener;

public class ActionTransmitterImpl implements ActionTransmitter {
    private final ChessClient chessClient = new ChessClient();

    private final Handler uiThreadHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

    private final AtomicBoolean eventListenerRunning = new AtomicBoolean(false);

    private MoveListener moveListener = null;
    private CastlingListener castlingListener = null;
    private EnPassantListener enPassantListener = null;
    private MessageListener messageListener = null;
    private RoomFullListener roomFullListener = null;

    private void runOnSendThread(Runnable runnable) {
        singleThreadExecutor.execute(runnable);
    }

    private void runOnUIThread(Runnable runnable) {
        uiThreadHandler.post(runnable);
    }

    private void processEvent(Object event) {
        if (event instanceof Move && moveListener != null) {
            Move move = (Move) event;
            moveListener.onMakeMove(move.oldX, move.oldY, move.newX, move.newY);
        } else if (event instanceof EnPassant && enPassantListener != null) {
            EnPassant enPassant = (EnPassant) event;
            enPassantListener.onEnPassant(enPassant.x, enPassant.y);
        } else if (event instanceof Castling && castlingListener != null) {
            Castling castling = (Castling) event;
            castlingListener.onCastling(castling.longCastling);
        } else if (event instanceof Message && messageListener != null) {
            Message message = (Message) event;
            messageListener.onNewMessage(message.text);
        } else if (event instanceof ServiceMessage && roomFullListener != null) {
            ServiceMessage serviceMessage = (ServiceMessage) event;
            if (serviceMessage == ServiceMessage.ROOM_FULL)
                roomFullListener.onFull();
        }
    }

    private void startEventListener() {
        if (eventListenerRunning.compareAndSet(false, true))
            new Thread(() -> {
                chessClient.streamEvents(event ->
                        runOnUIThread(() -> processEvent(event))
                );
                eventListenerRunning.set(false);
            }).start();
    }

    public void setRoomFullListener(RoomFullListener roomFullListener) {
        this.roomFullListener = roomFullListener;
    }

    public void removeRoomFullListener() {
        this.roomFullListener = null;
    }

    @Override
    public void setOnEnPassantListener(EnPassantListener enPassantListener) {
        this.enPassantListener = enPassantListener;
    }

    @Override
    public void setOnCastlingListener(CastlingListener castlingListener) {
        this.castlingListener = castlingListener;
    }

    @Override
    public void setOnMakeMoveListener(MoveListener moveListener) {
        this.moveListener = moveListener;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public void connect(Listener success, Listener error) {
        eventListenerRunning.set(false);
        runOnSendThread(() -> {
            boolean connected = chessClient.connect("chess.typex.one", 8081);
            runOnUIThread(() -> {
                if (connected)
                    success.invoke();
                else
                    error.invoke();
            });
        });
    }

    public void join(String key, Listener success, Listener error) {
        runOnSendThread(() -> {
            boolean joined = chessClient.join(key);
            if (joined)
                startEventListener();
            runOnUIThread(() -> {
                if (joined)
                    success.invoke();
                else
                    error.invoke();
            });
        });
    }

    public void createRoom(OnRoomCreated listener, Listener error) {
        runOnSendThread(() -> {
            String key = chessClient.newRoom();
            boolean created = !key.equals("ERROR");
            if (created)
                startEventListener();
            runOnUIThread(() -> {
                if (created)
                    listener.onRoomCreated(key);
                else
                    error.invoke();
            });
        });
    }

    @Override
    public void makeMove(int xOld, int yOld, int xNew, int yNew) {
        runOnSendThread(() -> chessClient.move(xOld, yOld, xNew, yNew));
    }

    @Override
    public void enPassant(int x, int y) {
        runOnSendThread(() -> chessClient.enPassant(x, y));
    }

    @Override
    public void castling(boolean longCastling) {
        runOnSendThread(() -> chessClient.castling(longCastling));
    }

    public void sendMessage(String text) {
        runOnSendThread(() -> chessClient.sendMessage(text));
    }

    public void unbind() {
        runOnSendThread(chessClient::disconnect);
        eventListenerRunning.set(false);
        castlingListener = null;
        messageListener = null;
        roomFullListener = null;
        enPassantListener = null;
        moveListener = null;
    }
}
