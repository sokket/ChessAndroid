package com.example.chess;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chess.game.ActionTransmitter;
import com.example.chess.game.ChessGame;
import com.example.chess.game.ChessView;
import com.example.chess.game.LogLine;
import com.example.chess.game.OnPressListener;
import com.example.chess.game.ResetOnPressListener;
import com.example.chess.game.TileType;
import com.example.chess.net.ActionTransmitterImpl;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements ChessView {

    private OnPressListener onPressListener;
    private ResetOnPressListener resetOnPressListener;
    private RecyclerView recyclerView;
    private final CardView[][] views = new CardView[8][8];

    private int whiteColor;
    private int blackColor;
    private int highLightColor;

    ChessGame chessGame;

    private Button join;
    private Button create;


    private void initColors() {
        whiteColor = ContextCompat.getColor(this, R.color.white);
        blackColor = ContextCompat.getColor(this, R.color.black);
        highLightColor = ContextCompat.getColor(this, R.color.purple_200);
    }

    private void loadViews(boolean whiteGame) {
        GridLayout grid = findViewById(R.id.grid);
        grid.removeAllViewsInLayout();

        Button button = findViewById(R.id.button);
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        for (int i = whiteGame ? 0 : 7; whiteGame ? i < 8 : i >= 0; i += whiteGame ? 1 : -1)
            for (int j = whiteGame ? 0 : 7; whiteGame ? j < 8 : j >= 0; j += whiteGame ? 1 : -1) {
                CardView tileView = (CardView) layoutInflater.inflate(R.layout.tile, grid, false);
                grid.addView(tileView);

                int finalJ = j;
                int finalI = i;
                tileView.setOnClickListener(v -> onPressListener.onPress(finalJ, finalI));

                views[i][j] = tileView;
            }
        button.setBackgroundColor(Color.RED);
        button.setOnClickListener(v -> resetOnPressListener.reset());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new GameLogAdapter(LayoutInflater.from(this)));
        initColors();

        ActionTransmitterImpl actionTransmitter = new ActionTransmitterImpl();

        EditText keyPrompt = findViewById(R.id.key_prompt);
        join = findViewById(R.id.join_btn);
        join.setOnClickListener(v -> actionTransmitter.connect(
                "oceancraft.ru",
                8081,
                () -> actionTransmitter.join(
                        keyPrompt.getText().toString(),
                        () -> startNetworkGame(actionTransmitter, false),
                        () -> Toast.makeText(this, "Error join to server", Toast.LENGTH_SHORT).show()
                ),
                () -> Toast.makeText(this, "Error connect to server", Toast.LENGTH_SHORT).show()
        ));

        create = findViewById(R.id.create_btn);
        create.setOnClickListener(v -> actionTransmitter.connect(
                "oceancraft.ru",
                8081,
                () -> actionTransmitter.createRoom(key -> {
                            Snackbar.make(findViewById(R.id.mainView), "Room created: " + key, Snackbar.LENGTH_LONG)
                                    .setAction("COPY", sv -> {
                                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData clip = ClipData.newPlainText("ChessGame", key);
                                        clipboard.setPrimaryClip(clip);
                                    })
                                    .show();
                            startNetworkGame(actionTransmitter, true);
                        },
                        () -> Toast.makeText(this, "Error creating room", Toast.LENGTH_SHORT).show()
                ),
                () -> Toast.makeText(this, "Error connect to server", Toast.LENGTH_SHORT).show()
        ));

        Button offlineButton = findViewById(R.id.offline_btn);
        offlineButton.setOnClickListener(v -> startOfflineGame());


    }

    void startOfflineGame() {
        chessGame = new ChessGame(this);
        loadViews(true);
        chessGame.initGame();
    }

    void startNetworkGame(ActionTransmitter actionTransmitter, boolean white) {
        chessGame = new ChessGame(this, actionTransmitter, white);
        loadViews(white);
        chessGame.initGame();
        join.setVisibility(View.INVISIBLE);
        create.setVisibility(View.INVISIBLE);
        findViewById(R.id.key_prompt_layout).setVisibility(View.INVISIBLE);
    }

    void showSnakeBar(String str) {
        Snackbar.make(findViewById(R.id.mainView), str, Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void onHighLight(int x, int y, boolean isHighLighted, boolean isBlack) {
        int realColor = isBlack ? blackColor : whiteColor;
        int color = isHighLighted ? highLightColor : realColor;
        views[y][x].setCardBackgroundColor(color);
    }

    @Override
    public void onChangeTile(int x, int y, TileType type) {
        AndroidTileType androidTileType = AndroidTileType.getByTileType(type);
        ImageView imageView = views[y][x].findViewById(R.id.img);
        if (androidTileType.getValue() != -1) {
            imageView.setVisibility(View.VISIBLE);
            Glide.with(this).load(androidTileType.getValue()).into(imageView);
        } else {
            imageView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void setOnPressListener(OnPressListener onPressListener) {
        this.onPressListener = onPressListener;
    }

    @Override
    public void setResetOnPressListener(ResetOnPressListener resetOnPressListener) {
        this.resetOnPressListener = resetOnPressListener;
    }

    @Override
    public void onNewLogLine(LogLine logLine) {
        GameLogAdapter adapter = (GameLogAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.addLine(logLine);
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        }
    }

    @Override
    public void cleanLog() {
        GameLogAdapter adapter = (GameLogAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.clean();
        }
    }

    @Override
    public void onMoveFinished(boolean whiteTurn) {
        loadViews(whiteTurn);
    }

    @Override
    public void onCheck() {
        showSnakeBar("CHECK");
    }

    @Override
    public void onCheckmate() {
        showSnakeBar("CHECKMATE");
    }

}