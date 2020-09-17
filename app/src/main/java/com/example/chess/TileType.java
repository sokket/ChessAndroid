package com.example.chess;

import androidx.annotation.DrawableRes;

public enum TileType {
    LIGHT_BISHOP(R.drawable.ic_chess_blt45),
    LIGHT_KING(R.drawable.ic_chess_klt45),
    LIGHT_KNIGHT(R.drawable.ic_chess_nlt45),
    LIGHT_PAWN(R.drawable.ic_chess_plt45),
    LIGHT_QUEEN(R.drawable.ic_chess_qlt45),
    LIGHT_ROOK(R.drawable.ic_chess_rlt45),
    BLANK(-1),
    BLACK_BISHOP(R.drawable.ic_chess_bdt45),
    BLACK_KING(R.drawable.ic_chess_kdt45),
    BLACK_KNIGHT(R.drawable.ic_chess_ndt45),
    BLACK_PAWN(R.drawable.ic_chess_pdt45),
    BLACK_QUEEN(R.drawable.ic_chess_qdt45),
    BLACK_ROOK(R.drawable.ic_chess_rdt45);

    @DrawableRes
    private final int value;

    TileType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
