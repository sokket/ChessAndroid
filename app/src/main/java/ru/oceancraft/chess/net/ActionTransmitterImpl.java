package ru.oceancraft.chess.net;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ru.oceancraft.chess.model.ActionTransmitter;
import ru.oceancraft.chess.model.CastlingListener;
import ru.oceancraft.chess.model.EnPassantListener;
import ru.oceancraft.chess.model.MoveListener;

public class ActionTransmitterImpl implements ActionTransmitter {
    private final ChessClient chessClient = new ChessClient();

    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            2,
            2,
            0,
            TimeUnit.MICROSECONDS,
            new ArrayBlockingQueue<>(10)
    );

    private MoveListener moveListener = null;
    private CastlingListener castlingListener = null;
    private EnPassantListener enPassantListener = null;
    private MessageListener messageListener = null;
    private RoomFullListener roomFullListener = null;

    private boolean eventListenerStarted = false;

    private void runOnThreadPool(Runnable runnable) {
        threadPoolExecutor.execute(runnable);
    }

    private void runOnUIThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
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
        if (!eventListenerStarted) {
            eventListenerStarted = true;
            runOnThreadPool(
                    () -> chessClient.streamEvents(
                            event -> runOnUIThread(
                                    () -> processEvent(event))));
        }
    }

    public void setRoomFullListener(RoomFullListener roomFullListener) {
        startEventListener();
        this.roomFullListener = roomFullListener;
    }

    public void removeRoomFullListener() {
        this.roomFullListener = null;
    }

    @Override
    public void setOnEnPassantListener(EnPassantListener enPassantListener) {
        startEventListener();
        this.enPassantListener = enPassantListener;
    }

    @Override
    public void setOnCastlingListener(CastlingListener castlingListener) {
        startEventListener();
        this.castlingListener = castlingListener;
    }

    @Override
    public void setOnMakeMoveListener(MoveListener moveListener) {
        startEventListener();
        this.moveListener = moveListener;
    }

    public void setMessageListener(MessageListener messageListener) {
        startEventListener();
        this.messageListener = messageListener;
    }

    public void connect(Listener success, Listener error) {
        eventListenerStarted = false;
        runOnThreadPool(() -> {
            boolean connect = chessClient.connect("oceancraft.ru", 8081);
            runOnUIThread(() -> {
                if (connect) {
                    success.invoke();
                } else
                    error.invoke();
            });
        });
    }

    public void join(String key, Listener success, Listener error) {
        runOnThreadPool(() -> {
            boolean join = chessClient.join(key);
            runOnUIThread(() -> {
                if (join) success.invoke();
                else error.invoke();
            });
        });
    }

    public void createRoom(OnRoomCreated listener, Listener error) {
        runOnThreadPool(() -> {
            String key = chessClient.newRoom();
            runOnUIThread(() -> {
                if (!key.equals("ERROR")) {
                    listener.onRoomCreated(key);
                } else
                    error.invoke();
            });
        });
    }

    @Override
    public void makeMove(int xOld, int yOld, int xNew, int yNew) {
        runOnThreadPool(() -> chessClient.move(xOld, yOld, xNew, yNew));
    }

    @Override
    public void enPassant(int x, int y) {
        runOnThreadPool(() -> chessClient.enPassant(x, y));
    }

    @Override
    public void castling(boolean longCastling) {
        runOnThreadPool(() -> chessClient.castling(longCastling));
    }

    public void sendMessage(String text) {
        runOnThreadPool(() -> chessClient.sendMessage(text));
    }

    public void unbind() {
        runOnThreadPool(chessClient::disconnect);
        eventListenerStarted = false;

        castlingListener = null;
        messageListener = null;
        roomFullListener = null;
        enPassantListener = null;
        moveListener = null;
    }
}
