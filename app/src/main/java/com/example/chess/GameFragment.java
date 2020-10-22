package com.example.chess;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.bumptech.glide.Glide;
import com.example.chess.game.*;
import com.example.chess.net.ActionTransmitterImpl;
import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

public class GameFragment extends Fragment implements ChessView {

    @Inject
    ActionTransmitterImpl actionTransmitter;

    private OnPressListener onPressListener;
    private final CardView[][] views = new CardView[8][8];

    private ViewPager2 viewPager;
    private GameViewModel gameViewModel;

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

        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void onStop() {
        super.onStop();
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        whiteColor = ContextCompat.getColor(requireContext(), R.color.whiteTile);
        blackColor = ContextCompat.getColor(requireContext(), R.color.blackTile);
        highLightColor = ContextCompat.getColor(requireContext(), R.color.purple_200);

        boolean netGame = requireArguments().getBoolean("net");
        boolean isWhite = requireArguments().getBoolean("white");

        if (netGame)
            startNetworkGame(actionTransmitter, isWhite);
        else
            startOfflineGame();

        viewPager = view.findViewById(R.id.pager);
        FragmentStateAdapter pagerAdapter = new GamePagerAdapter(this, netGame);
        viewPager.setAdapter(pagerAdapter);
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
        actionTransmitter.setMessageListener(text -> {
            if (viewPager.getCurrentItem() == 0)
                Snackbar.make(requireView(), text, Snackbar.LENGTH_LONG)
                        .setAction("answer", v -> viewPager.setCurrentItem(1, true))
                        .show();
            gameViewModel.addMessage(new Message(text, false));
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

    }

    @Override
    public void onNewLogLine(LogLine logLine) {
        gameViewModel.addLogLine(logLine);
    }

    @Override
    public void cleanLog() {

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
        gameViewModel.clearLogs();
        gameViewModel.clearMessages();
    }
}