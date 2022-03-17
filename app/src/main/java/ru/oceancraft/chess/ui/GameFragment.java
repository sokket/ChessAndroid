package ru.oceancraft.chess.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.inject.Inject;

import ru.oceancraft.chess.App;
import ru.oceancraft.chess.R;
import ru.oceancraft.chess.model.ChessGame;
import ru.oceancraft.chess.model.ChessView;
import ru.oceancraft.chess.model.GameState;
import ru.oceancraft.chess.model.LogLine;
import ru.oceancraft.chess.model.Message;
import ru.oceancraft.chess.model.Position;
import ru.oceancraft.chess.model.listeners.OnPressListener;
import ru.oceancraft.chess.model.listeners.OnPromotionListener;
import ru.oceancraft.chess.model.listeners.ResetOnPressListener;
import ru.oceancraft.chess.model.TileType;
import ru.oceancraft.chess.net.NetworkActionTransmitter;
import ru.oceancraft.chess.presentation.GamePagerAdapter;
import ru.oceancraft.chess.presentation.GameViewModel;

public class GameFragment extends Fragment implements ChessView {

    @Inject
    NetworkActionTransmitter actionTransmitter;

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

        if (savedInstanceState != null) {
            try {
                byte[] savedGameState = savedInstanceState.getByteArray("state");
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(savedGameState);
                GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                byte[] buffer = new byte[1024];
                int len;

                while ((len = gzipInputStream.read(buffer)) > 0) {
                    byteArrayOutputStream.write(buffer, 0, len);
                }

                int end = 0;
                for (int i = 0; i < buffer.length; i++) {
                    if (buffer[i] == 0) {
                        end = i;
                        break;
                    }
                }

                String json = new String(buffer, 0, end);
                Gson gson = new Gson();
                GameState gameState = gson.fromJson(json, GameState.class);

                if (!gameState.isNetMode()) {
                    loadViews(gameState.isWhiteMove());
                }
                chessGame.loadState(gameState);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            gameViewModel.clearLogs();
        }

        viewPager = view.findViewById(R.id.pager);
        FragmentStateAdapter pagerAdapter = new GamePagerAdapter(this, netGame, isWhite);
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

    void startNetworkGame(NetworkActionTransmitter actionTransmitter, boolean isWhite) {
        actionTransmitter.setMessageListener(text -> {
            if (viewPager.getCurrentItem() == 0)
                Snackbar.make(requireView(), text, Snackbar.LENGTH_LONG)
                        .setAction("answer", v -> viewPager.setCurrentItem(1, true))
                        .show();
            gameViewModel.addMessage(new Message(text, false));
        });

        actionTransmitter.setOnStatusCheckListener(() -> gameViewModel.connectionCheckFinished());

        chessGame = new ChessGame(this, actionTransmitter, isWhite);
        loadViews(isWhite);
        chessGame.initGame();
        gameViewModel.showTurn(true);
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
    public void onLocalGameMoveFinished(boolean whiteTurn) {
        loadViews(whiteTurn);
    }

    @Override
    public void onCheck() {

    }

    @Override
    public void onCheckmate() {

    }

    @Override
    public void onNetGameMoveFinished(boolean whiteTurn) {
        gameViewModel.showTurn(whiteTurn);
    }

    @Override
    public void promotionChoiceRequirement(Position viewPos, boolean whiteGame, OnPromotionListener onPromotionListener) {
        final Map<String, TileType> tileTypeMap = new HashMap<String, TileType>() {{
            put("Queen", whiteGame ? TileType.WHITE_QUEEN : TileType.BLACK_QUEEN);
            put("Knight", whiteGame ? TileType.WHITE_KNIGHT : TileType.BLACK_KNIGHT);
            put("Rook", whiteGame ? TileType.WHITE_ROOK : TileType.BLACK_ROOK);
            put("Bishop", whiteGame ? TileType.WHITE_BISHOP : TileType.BLACK_BISHOP);
        }};

        View target = views[viewPos.y][viewPos.x];
        PopupMenu menu = new PopupMenu(requireContext(), target);

        for (String name : tileTypeMap.keySet()) {
            menu.getMenu().add(name);
        }

        menu.setOnMenuItemClickListener(item -> {
            String title = item.getTitle().toString();
            TileType tileType = tileTypeMap.getOrDefault(title, whiteGame ? TileType.WHITE_QUEEN : TileType.BLACK_QUEEN);
            onPromotionListener.onSelectTileType(tileType);
            return true;
        });

        menu.show();
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        try {
            Gson gson = new Gson();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream zipStream = new GZIPOutputStream(byteArrayOutputStream);
            zipStream.write(gson.toJson(chessGame.exportState()).getBytes());
            zipStream.close();
            byteArrayOutputStream.close();
            byte[] bytes = byteArrayOutputStream.toByteArray();
            outState.putByteArray("state", bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        actionTransmitter.close();
        super.onDestroy();
    }
}