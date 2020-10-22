package com.example.chess;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chess.net.ActionTransmitterImpl;

import javax.inject.Inject;


public class ChatFragment extends Fragment {

    @Inject
    ActionTransmitterImpl actionTransmitter;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App app = (App) requireActivity().getApplication();
        app.appComponent.inject(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        ChatAdapter adapter = new ChatAdapter(LayoutInflater.from(requireContext()));
        recyclerView.setAdapter(adapter);

        GameViewModel gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
        gameViewModel.getMessages().observe(this, messages -> {
            adapter.updateData(messages);
            if (!messages.isEmpty())
                recyclerView.scrollToPosition(messages.size() - 1);
        });

        EditText editText = view.findViewById(R.id.messageText);
        ImageButton sendButton = view.findViewById(R.id.sendBtn);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty())
                    sendButton.setVisibility(View.GONE);
                else
                    sendButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        sendButton.setOnClickListener(v -> {
            String messageText = editText.getText().toString();
            if (!messageText.trim().isEmpty()) {
                editText.setText("");
                actionTransmitter.sendMessage(messageText);
                gameViewModel.addMessage(new Message(messageText, true));
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }
}