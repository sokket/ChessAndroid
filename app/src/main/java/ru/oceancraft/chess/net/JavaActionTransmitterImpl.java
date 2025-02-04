package ru.oceancraft.chess.net;

import android.os.Handler;
import android.os.Looper;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.oceancraft.chess.model.listeners.CastlingListener;
import ru.oceancraft.chess.model.listeners.EnPassantListener;
import ru.oceancraft.chess.model.listeners.MoveListener;
import ru.oceancraft.chess.model.listeners.PromotionListener;

public class JavaActionTransmitterImpl implements NetworkActionTransmitter {

    private final Handler uiThreadHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Thread listener;

    private MoveListener moveListener;
    private MessageListener messageListener;
    private EnPassantListener enPassantListener;
    private CastlingListener castlingListener;
    private PromotionListener promotionListener;
    private RoomFullListener roomFullListener;
    private StatusCheckListener statusCheckListener;

    private final Timer statusCheckTimer = new Timer();

    private void runOnUIThread(Runnable runnable) {
        uiThreadHandler.post(runnable);
    }

    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    private void executeNet(Listener error, ThrowsIO run) {
        executor.execute(() -> {
            try {
                run.run();
            } catch (IOException | NullPointerException e) {
                runOnUIThread(error::invoke);
                e.printStackTrace();
            }
        });
    }

    private void executeNet(ThrowsIO run) {
        executor.execute(() -> {
            try {
                run.run();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        });
    }

    private interface ThrowsIO {
        void run() throws IOException;
    }

    @Override
    public void setRoomFullListener(RoomFullListener roomFullListener) {
        this.roomFullListener = roomFullListener;
    }

    @Override
    public void removeRoomFullListener() {
        roomFullListener = null;
    }

    @Override
    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    @Override
    public void setOnStatusCheckListener(StatusCheckListener statusCheckListener) {
        this.statusCheckListener = statusCheckListener;
    }

    private void runListener() {
        listener = new Thread(() -> {
            byte[] response = new byte[1];
            while (!Thread.interrupted()) {
                try {
                    if (inputStream.read(response, 0, 1) != 1) {
                        throw new IOException();
                    }
                    switch (Packages.valueOf(response[0])) {
                        case MOVE:
                            byte[] moveBytes = new byte[4];
                            if (inputStream.read(moveBytes, 0, 4) != 4) {
                                throw new IOException();
                            }
                            if (moveListener != null) {
                                runOnUIThread(() -> moveListener.onMakeMove(
                                        moveBytes[0],
                                        moveBytes[1],
                                        moveBytes[2],
                                        moveBytes[3]
                                ));
                            }
                            break;
                        case CASTLING:
                            boolean longCastling = inputStream.read() == 1;
                            if (castlingListener != null) {
                                runOnUIThread(() -> castlingListener.onCastling(longCastling));
                            }
                            break;
                        case EN_PASSANT:
                            final int x = inputStream.read();
                            final int y = inputStream.read();
                            if (enPassantListener != null) {
                                runOnUIThread(() -> enPassantListener.onEnPassant(x, y));
                            }
                            break;
                        case PROMOTION:
                            final int oX = inputStream.read();
                            final int oY = inputStream.read();
                            final int nX = inputStream.read();
                            final int nY = inputStream.read();
                            final int tileType = inputStream.read();
                            if (promotionListener != null) {
                                runOnUIThread(() -> promotionListener.onPromotion(oX, oY, nX, nY, tileType));
                            }
                            break;
                        case CHAT_MSG:
                            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                            byte read;
                            while ((read = (byte) inputStream.read()) != 0) {
                                byteArray.write(read);
                            }
                            byte[] msgBytes = byteArray.toByteArray();
                            String message = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(msgBytes)).toString();
                            if (messageListener != null) {
                                runOnUIThread(() -> messageListener.onNewMessage(message));
                            }
                            break;
                        case JOIN_ROOM:
                            if (roomFullListener != null) {
                                runOnUIThread(() -> roomFullListener.onFull());
                            }
                            executeNet(() -> {
                                outputStream.write(Packages.PING.num);
                                outputStream.flush();
                            });
                            break;
                        case PING:
                            if (statusCheckListener != null) {
                                statusCheckListener.onCheckFinished();
                            }
                            statusCheckTimer.schedule((new TimerTask() {
                                @Override
                                public void run() {
                                    executeNet(() -> {
                                        outputStream.write(Packages.PONG.num);
                                        outputStream.flush();
                                    });
                                }
                            }), 3000L);
                            break;
                        case PONG:
                            if (statusCheckListener != null) {
                                statusCheckListener.onCheckFinished();
                            }
                            statusCheckTimer.schedule((new TimerTask() {
                                @Override
                                public void run() {
                                    executeNet(() -> {
                                        outputStream.write(Packages.PING.num);
                                        outputStream.flush();
                                    });
                                }
                            }), 3000L);
                            break;
                    }
                } catch (SocketException e) {
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        listener.start();
    }

    @Override
    public void connect(Listener success, Listener onError) {
        close();
        executeNet(onError, () -> {
            socket = new Socket("chess-game.typex.one", 8081);
            outputStream = new BufferedOutputStream(socket.getOutputStream());
            String header = "CHESS_PROTO/1.0";
            ByteBuffer byteBuffer = StandardCharsets.ISO_8859_1.encode(header);
            outputStream.write(byteBuffer.array());
            outputStream.flush();
            inputStream = socket.getInputStream();
            runOnUIThread(success::invoke);
        });
    }

    @Override
    public void join(String key, Listener success, Listener error) {
        executeNet(error, () -> {
            outputStream.write(Packages.JOIN_ROOM.num);
            byte[] inviteCode = StandardCharsets.ISO_8859_1.encode(key).array();
            outputStream.write(inviteCode);
            outputStream.flush();
            byte[] response = new byte[1];
            if (inputStream.read(response, 0, 1) != 1
                    || response[0] != Packages.JOINED.num
            ) {
                throw new IOException();
            }
            runOnUIThread(success::invoke);
            runListener();
        });
    }

    @Override
    public void createRoom(OnRoomCreated success, Listener error) {
        executeNet(error, () -> {
            outputStream.write(Packages.CREATE_ROOM.num);
            outputStream.write(1);
            outputStream.flush();
            if (inputStream.skip(1) != 1) {
                throw new IOException();
            }
            byte[] inviteCodeBytes = new byte[7];
            if (inputStream.read(inviteCodeBytes, 0, 7) != 7) {
                throw new IOException();
            } else {
                String inviteCode = new String(inviteCodeBytes, StandardCharsets.UTF_8);
                runOnUIThread(() -> success.onRoomCreated(inviteCode));
                runListener();
            }
        });
    }

    @Override
    public void sendMessage(String text) {
        executeNet(() -> {
            outputStream.write(Packages.CHAT_MSG.num);
            byte[] encoded = StandardCharsets.UTF_8.encode(text).array();
            outputStream.write(encoded);
            outputStream.write(0);
            outputStream.flush();
        });
    }

    @Override
    public void unbind() {
        executeNet(() -> {
            messageListener = null;
            enPassantListener = null;
            castlingListener = null;
            moveListener = null;
            roomFullListener = null;
            statusCheckListener = null;
            promotionListener = null;
        });
    }

    @Override
    public void setOnMakeMoveListener(MoveListener eventListener) {
        this.moveListener = eventListener;
    }

    @Override
    public void makeMove(int xOld, int yOld, int xNew, int yNew) {
        executeNet(() -> {
            outputStream.write(Packages.MOVE.num);
            outputStream.write(new byte[]{
                    (byte) xOld,
                    (byte) yOld,
                    (byte) xNew,
                    (byte) yNew
            });
            outputStream.flush();
        });
    }

    @Override
    public void enPassant(int x, int y) {
        executeNet(() -> {
            outputStream.write(Packages.EN_PASSANT.num);
            outputStream.write(new byte[]{(byte) x, (byte) y});
            outputStream.flush();
        });
    }

    @Override
    public void castling(boolean longCastling) {
        executeNet(() -> {
            outputStream.write(Packages.CASTLING.num);
            outputStream.write(longCastling ? 1 : 0);
            outputStream.flush();
        });
    }

    @Override
    public void promotion(int xOld, int yOld, int xNew, int yNew, char newTileType) {
        executeNet(() -> {
            outputStream.write(Packages.PROMOTION.num);
            outputStream.write(new byte[] {
                    (byte) xOld,
                    (byte) yOld,
                    (byte) xNew,
                    (byte) yNew,
                    (byte) newTileType
            });
            outputStream.flush();
        });
    }

    @Override
    public void setOnPromotionListener(PromotionListener promotionListener) {
        this.promotionListener = promotionListener;
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
    public void close() {
        unbind();
        executeNet(() -> {
            if (inputStream != null) {
                inputStream.close();
                inputStream = null;
            }
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
            if (socket != null) {
                socket.close();
                socket = null;
            }
            if (listener != null) {
                listener.interrupt();
                listener = null;
            }
        });
    }
}
