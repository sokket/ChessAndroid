package ru.oceancraft.chess.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

import ru.oceancraft.chess.App;
import ru.oceancraft.chess.R;
import ru.oceancraft.chess.Screens;
import ru.oceancraft.chess.net.NetworkActionTransmitter;
import ru.terrakok.cicerone.Router;


public class WaitingFragment extends Fragment {

    @Inject
    NetworkActionTransmitter actionTransmitter;

    @Inject
    Router router;

    private boolean joined = false;

    public WaitingFragment() {
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
        return inflater.inflate(R.layout.fragment_waiting, container, false);
    }

    void showMessage(String text) {
        View view = getView();
        if (view != null) {
            Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show();
        }
    }

    void onRoomCreated(String key) {
        actionTransmitter.setRoomFullListener(() -> {
            joined = true;
            router.replaceScreen(new Screens.GameScreen(true, true));
        });

        TextView codeView = requireView().findViewById(R.id.invite_code);
        Button copyButton = requireView().findViewById(R.id.copy_btn);

        codeView.setText(key);
        copyButton.setOnClickListener(v -> {
            ClipboardManager clipboard =
                    (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("ChessGame", key);
            clipboard.setPrimaryClip(clip);
            Snackbar.make(requireView(), "Copied", Snackbar.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        actionTransmitter.connect(
                () -> actionTransmitter.createRoom(this::onRoomCreated,
                        () -> {
                            showMessage("Can't create room");
                            router.exit();
                        }
                ),
                () -> {
                    showMessage("Can't connect to server");
                    router.navigateTo(new Screens.LaunchScreen());
                }
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        actionTransmitter.removeRoomFullListener();
        if (!joined)
            actionTransmitter.close();
    }
}