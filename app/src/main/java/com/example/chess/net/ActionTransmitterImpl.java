package com.example.chess.net;

import android.os.Handler;
import android.os.Looper;

import com.example.chess.game.ActionTransmitter;
import com.example.chess.game.MoveListener;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ActionTransmitterImpl implements ActionTransmitter {
    private final ChessClient chessClient;

    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            3,
            4,
            1,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(10)
    );

    private void runOnThreadPool(Runnable runnable) {
        threadPoolExecutor.execute(runnable);
    }

    private void runOnUIThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    public ActionTransmitterImpl() {
        chessClient = new ChessClient();
    }

    public void connect(String address, int port, Listener success, Listener error) {
        runOnThreadPool(() -> {
            boolean connect = chessClient.connect(address, port);
            runOnUIThread(() -> {
                if (connect)
                    success.invoke();
                else
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
                if (!key.equals("ERROR"))
                    listener.onRoomCreated(key);
                else
                    error.invoke();
            });
        });
    }

    @Override
    public void setOnMakeMoveListener(MoveListener moveListener) {
        runOnThreadPool(() ->
                chessClient.streamMoves((xOld, yOld, xNew, yNew) ->
                        runOnUIThread(() ->
                                moveListener.onMakeMove(xOld, yOld, xNew, yNew)
                        )
                ));
    }

    @Override
    public void makeMove(int xOld, int yOld, int xNew, int yNew) {
        runOnThreadPool(() -> chessClient.move(xOld, yOld, xNew, yNew));
    }
}
