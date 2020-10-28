package ru.oceancraft.chess;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.oceancraft.chess.R;

import javax.inject.Inject;

import ru.terrakok.cicerone.Router;

public class LaunchFragment extends Fragment {

    @Inject
    Router router;

    public LaunchFragment() {
        // Required empty public constructor
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
        return inflater.inflate(R.layout.fragment_launch, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button networkGameButton = view.findViewById(R.id.network_game_btn);
        networkGameButton.setOnClickListener(v ->
                router.navigateTo(new Screens.NetworkGameSetupScreen()));

        Button localGameButton = view.findViewById(R.id.local_game_btn);
        localGameButton.setOnClickListener(v ->
                router.navigateTo(new Screens.GameScreen(false, true)));
    }
}