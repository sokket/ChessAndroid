package com.example.chess;

import androidx.annotation.DrawableRes;

import com.example.chess.game.TileType;

public enum AndroidTileType {
    LIGHT_BISHOP(R.drawable.ic_chess_blt45, TileType.LIGHT_BISHOP),
    LIGHT_KING(R.drawable.ic_chess_klt45, TileType.LIGHT_KING),
    LIGHT_KNIGHT(R.drawable.ic_chess_nlt45, TileType.LIGHT_KNIGHT),
    LIGHT_PAWN(R.drawable.ic_chess_plt45, TileType.LIGHT_PAWN),
    LIGHT_QUEEN(R.drawable.ic_chess_qlt45, TileType.LIGHT_QUEEN),
    LIGHT_ROOK(R.drawable.ic_chess_rlt45, TileType.LIGHT_ROOK),
    BLANK(-1, TileType.BLANK),
    BLACK_BISHOP(R.drawable.ic_chess_bdt45, TileType.BLACK_BISHOP),
    BLACK_KING(R.drawable.ic_chess_kdt45, TileType.BLACK_KING),
    BLACK_KNIGHT(R.drawable.ic_chess_ndt45, TileType.BLACK_KNIGHT),
    BLACK_PAWN(R.drawable.ic_chess_pdt45, TileType.BLACK_PAWN),
    BLACK_QUEEN(R.drawable.ic_chess_qdt45, TileType.BLACK_QUEEN),
    BLACK_ROOK(R.drawable.ic_chess_rdt45, TileType.BLACK_ROOK);



    @DrawableRes
    private final int value;

    private final TileType tileType;

    public static AndroidTileType getByTileType(TileType tileType) {
        for (AndroidTileType value : AndroidTileType.values())
            if (value.tileType == tileType)
                return value;

        return AndroidTileType.BLANK;
    }

    public int getValue() {
        return value;
    }

    public TileType getTileType() {
        return tileType;
    }

    AndroidTileType(int value, TileType tileType) {
        this.value = value;
        this.tileType = tileType;
    }
}
