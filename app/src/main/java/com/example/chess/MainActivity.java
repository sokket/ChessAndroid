package com.example.chess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private Tile[][] gameBoard = new Tile[8][8];

    private boolean firstTurn = true;

    private int player1points = 0;
    private int player2points = 0;

    private TextView textViewPlayer1;
    private TextView textViewPlayer2;

    private int highLightSrcX;
    private int highLightSrcY;

    TileType startingLineup(int i, int j) {
        if (i == 1) return TileType.BLACK_PAWN;
        else if (i == 6) return TileType.LIGHT_PAWN;
        else if (i == 0 && (j == 0 || j == 7)) return TileType.BLACK_ROOK;
        else if (i == 7 && (j == 0 || j == 7)) return TileType.LIGHT_ROOK;
        else if (i == 0 && (j == 1 || j == 6)) return TileType.BLACK_KNIGHT;
        else if (i == 7 && (j == 1 || j == 6)) return TileType.LIGHT_KNIGHT;
        else if (i == 0 && (j == 2 || j == 5)) return TileType.BLACK_BISHOP;
        else if (i == 7 && (j == 2 || j == 5)) return TileType.LIGHT_BISHOP;
        else if (i == 0 && j == 3) return TileType.BLACK_QUEEN;
        else if (i == 7 && j == 4) return TileType.LIGHT_QUEEN;
        else if (i == 0 && j == 4) return TileType.BLACK_KING;
        else if (i == 7 && j == 3) return TileType.LIGHT_KING;
        else if (i == 5 && j == 6) return TileType.LIGHT_QUEEN;
        else if (i == 3 && j == 4) return TileType.BLACK_PAWN;
        return TileType.BLANK;
    }

    void reset() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                gameBoard[i][j].setTileType(startingLineup(i, j));
                gameBoard[i][j].setHighLighted(false);
            }
    }

    void clearHighLight() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                gameBoard[i][j].setHighLighted(false);
    }

    private List<Position> filterOverLap(List<Position> lp) {
        return lp.stream()
                .filter(p ->
                        p.getX() < 8 && p.getX() >= 0 &&
                                p.getY() < 8 && p.getY() >= 0 &&
                                gameBoard[p.getX()][p.getY()].getTileType() == TileType.BLANK
                )
                .collect(Collectors.toList());
    }

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
                final TileType figure = startingLineup(i, j);
                gameBoard[i][j] = new Tile(isBlack, figure, (isHighLighted) -> {
                    v.setCardBackgroundColor(isHighLighted ? highLightColor : (isBlack ? blackColor : whiteColor));
                }, tileType -> {
                    if (tileType.getValue() != -1) {
                        imageView.setVisibility(View.VISIBLE);
                        Glide.with(this).load(tileType.getValue()).into(imageView);
                    } else imageView.setVisibility(View.INVISIBLE);
                });
                int finalY = i;
                int finalX = j;
                v.setOnClickListener(l -> {
                    Toast.makeText(this, finalY + " " + finalX, Toast.LENGTH_LONG).show();
                    if (gameBoard[finalY][finalX].isLighted()) {
                        clearHighLight();
                        TileType tileType = gameBoard[highLightSrcY][highLightSrcX].getTileType();
                        gameBoard[highLightSrcY][highLightSrcX].setTileType(TileType.BLANK);
                        gameBoard[finalY][finalX].setTileType(tileType);
                    } else {
                        clearHighLight();
                        highLightSrcX = finalX;
                        highLightSrcY = finalY;
                        figure.getMovesFor(finalX, finalY, ((x, y) ->
                                x >= 0 && x < 8 &&
                                        y >= 0 && y < 8 &&
                                        gameBoard[y][x].getTileType() != TileType.BLANK
                        )).stream().filter(p ->
                                p.getX() < 8 && p.getX() >= 0 &&
                                        p.getY() < 8 && p.getY() >= 0
                        ).forEach(it ->
                                gameBoard[it.getY()][it.getX()].setHighLighted(true));
                    }
                });
            }
        }

        Button buttonReset = findViewById(R.id.buttonReset);
        buttonReset.setOnClickListener(v -> {
            reset();
        });
    }
}