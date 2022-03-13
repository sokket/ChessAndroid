package ru.oceancraft.chess.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.oceancraft.chess.R;
import ru.oceancraft.chess.presentation.GameLogAdapter;
import ru.oceancraft.chess.presentation.GameViewModel;


public class LogFragment extends Fragment {

    public LogFragment() {
        // Required empty public constructor
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        GameLogAdapter adapter = new GameLogAdapter(LayoutInflater.from(requireContext()));
        recyclerView.setAdapter(adapter);

        GameViewModel model = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
        model.getLogs().observe(getViewLifecycleOwner(), logLines -> {
            adapter.update(logLines);
            if (!logLines.isEmpty())
                recyclerView.scrollToPosition(logLines.size() - 1);
        });

        boolean netGame = false;
        boolean whiteGame = true;
        Bundle arguments = getArguments();
        if (arguments != null) {
            netGame = arguments.getBoolean("netGame", false);
            whiteGame = arguments.getBoolean("whiteGame", true);
        }

        Button turnIndicator = view.findViewById(R.id.turn);

        if (netGame) {
            boolean finalWhiteGame = whiteGame;
            model.getTurn().observe(getViewLifecycleOwner(), whiteTurn -> {
                if (finalWhiteGame ^ whiteTurn) {
                    turnIndicator.setEnabled(false);
                    turnIndicator.setText("Opponent's turn");
                } else {
                    turnIndicator.setEnabled(true);
                    turnIndicator.setText("Your turn");
                }
            });

            TextView connectionStatusText = view.findViewById(R.id.connectionCheckStatus);

            model.getTimeSinceLastConnectionCheck().observe(getViewLifecycleOwner(), seconds ->
                    connectionStatusText.setText(seconds.toString() + "s")
            );
        } else {
            turnIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log, container, false);
    }

    public static LogFragment newInstance(boolean netGame, boolean whiteGame) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("netGame", netGame);
        bundle.putBoolean("whiteGame", whiteGame);
        LogFragment logFragment = new LogFragment();
        logFragment.setArguments(bundle);
        return logFragment;
    }
}