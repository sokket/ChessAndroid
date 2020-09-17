package com.example.chess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    private Tile[][] gameBoard = new Tile[8][8];

    private boolean player1Turn = true;

    private int player1points = 0;
    private int player2points = 0;

    private TextView textViewPlayer1;
    private TextView textViewPlayer2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int whiteColor = ContextCompat.getColor(this, R.color.white);
        int blackColor = ContextCompat.getColor(this, R.color.black);
        int highLightColor = ContextCompat.getColor(this, R.color.purple_200);

        textViewPlayer1 = findViewById(R.id.text_view_p1);
        textViewPlayer2 = findViewById(R.id.text_view_p2);

        GridLayout grid = findViewById(R.id.grid);


        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                CardView v = (CardView) LayoutInflater.from(this).inflate(R.layout.tile, grid, false);
                grid.addView(v);
                ImageView imageView = v.findViewById(R.id.img);
                final boolean isBlack = ((i % 2 == 0 && j % 2 != 0) || (i % 2 != 0 && j % 2 == 0));
                Tile tile = new Tile(isBlack, TileType.BLACK_BISHOP, (isHighLighted) -> {
                    v.setCardBackgroundColor(isHighLighted ? highLightColor : (isBlack ? blackColor : whiteColor));
                }, tileType -> {
                    if (tileType.getValue() != -1)
                        Glide.with(this).load(tileType.getValue()).into(imageView);
                });
                gameBoard[i][j] = tile;
            }
        }

        Button buttonReset = findViewById(R.id.buttonReset);
        //buttonReset.setOnClickListener();
    }
}