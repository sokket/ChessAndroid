package com.example.chess;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chess.game.ChessGame;
import com.example.chess.game.ChessView;
import com.example.chess.game.LogLine;
import com.example.chess.game.OnPressListener;
import com.example.chess.game.ResetOnPressListener;
import com.example.chess.game.TileType;
import com.example.chess.net.ActionTransmitterImpl;
import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

public class GameFragment extends Fragment implements ChessView {

    @Inject
    ActionTransmitterImpl actionTransmitter;

    private OnPressListener onPressListener;
    private ResetOnPressListener resetOnPressListener;
    private RecyclerView recyclerView;
    private final CardView[][] views = new CardView[8][8];

    private int whiteColor;
    private int blackColor;
    private int highLightColor;

    ChessGame chessGame;

    public GameFragment() {
        // Required empty public constructor
    }

    public static GameFragment newInstance(boolean netGame, boolean isWhite) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("net", netGame);
        bundle.putBoolean("white", isWhite);
        GameFragment gameFragment = new GameFragment();
        gameFragment.setArguments(bundle);
        return gameFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        App app = (App) requireActivity().getApplication();
        app.appComponent.inject(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        whiteColor = ContextCompat.getColor(requireContext(), R.color.whiteTile);
        blackColor = ContextCompat.getColor(requireContext(), R.color.blackTile);
        highLightColor = ContextCompat.getColor(requireContext(), R.color.purple_200);

        recyclerView = view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new GameLogAdapter(LayoutInflater.from(requireContext())));

        boolean netGame = requireArguments().getBoolean("net");
        boolean isWhite = requireArguments().getBoolean("white");

        if (netGame)
            startNetworkGame(actionTransmitter, isWhite);
        else
            startOfflineGame();
    }

    private void loadViews(boolean whiteGame) {
        GridLayout grid = requireView().findViewById(R.id.grid);
        grid.removeAllViewsInLayout();

        LayoutInflater layoutInflater = LayoutInflater.from(requireContext());

        for (int i = whiteGame ? 0 : 7; whiteGame ? i < 8 : i >= 0; i += whiteGame ? 1 : -1)
            for (int j = whiteGame ? 0 : 7; whiteGame ? j < 8 : j >= 0; j += whiteGame ? 1 : -1) {
                CardView tileView = (CardView) layoutInflater.inflate(R.layout.tile, grid, false);
                grid.addView(tileView);

                int finalJ = j;
                int finalI = i;
                tileView.setOnClickListener(v -> onPressListener.onPress(finalJ, finalI));

                views[i][j] = tileView;
            }
    }

    void startOfflineGame() {
        chessGame = new ChessGame(this);
        loadViews(true);
        chessGame.initGame();
    }

    void startNetworkGame(ActionTransmitterImpl actionTransmitter, boolean isWhite) {
        actionTransmitter.setMessageListener(text ->
                Snackbar.make(requireView(), text, Snackbar.LENGTH_LONG).show());

        requireView().findViewById(R.id.message_send).setOnClickListener(v -> {
            EditText editText = requireView().findViewById(R.id.message_prompt);
            String text = editText.getText().toString();
            actionTransmitter.sendMessage(text);
        });


        chessGame = new ChessGame(this, actionTransmitter, isWhite);
        loadViews(isWhite);
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

    }

    @Override
    public void onCheckmate() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        actionTransmitter.unbind();
    }
}