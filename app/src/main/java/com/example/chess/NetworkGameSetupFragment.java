package com.example.chess;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chess.net.ActionTransmitterImpl;

import javax.inject.Inject;

import ru.terrakok.cicerone.Router;

public class NetworkGameSetupFragment extends Fragment {

    @Inject
    Router router;

    @Inject
    ActionTransmitterImpl actionTransmitter;

    public NetworkGameSetupFragment() {
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
        return inflater.inflate(R.layout.fragment_network_game_setup, container, false);
    }

    void showToast(String text) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView inviteCodeView = view.findViewById(R.id.key_prompt);
        Button joinBtn = view.findViewById(R.id.join_btn);
        joinBtn.setOnClickListener(v -> actionTransmitter.connect(
                "oceancraft.ru",
                8081,
                () -> actionTransmitter.join(
                        inviteCodeView.getText().toString(),
                        () -> router.navigateTo(new Screens.GameScreen(true, false)),
                        () -> {
                            showToast("Can't join room");
                            router.exit();
                        }
                ),
                () -> {
                    showToast("Can't connect to server");
                    router.exit();
                }
        ));

        Button createBtn = view.findViewById(R.id.create_btn);
        createBtn.setOnClickListener(v ->
                router.navigateTo(new Screens.WaitingScreen()));
    }
}