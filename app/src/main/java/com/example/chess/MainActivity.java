package com.example.chess;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.chess.game.ChessGame;
import com.example.chess.game.ChessView;
import com.example.chess.game.OnPressListener;
import com.example.chess.game.TileType;

public class MainActivity extends AppCompatActivity implements ChessView {

    private OnPressListener onPressListener;
    private final CardView[][] views = new CardView[8][8];

    private int whiteColor;
    private int blackColor;
    private int highLightColor;

    private void initColors() {
        whiteColor = ContextCompat.getColor(this, R.color.white);
        blackColor = ContextCompat.getColor(this, R.color.black);
        highLightColor = ContextCompat.getColor(this, R.color.purple_200);
    }

    private void loadViews() {
        GridLayout grid = findViewById(R.id.grid);
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                CardView tileView = (CardView) layoutInflater.inflate(R.layout.tile, grid, false);
                grid.addView(tileView);

                int finalJ = j;
                int finalI = i;
                tileView.setOnClickListener(v -> onPressListener.onPress(finalJ, finalI));

                views[i][j] = tileView;
            }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initColors();
        ChessGame chessGame = new ChessGame(this);
        loadViews();
        chessGame.initGame();
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
}