package ru.oceancraft.chess;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oceancraft.chess.R;


public class LogFragment extends Fragment {

    public LogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        GameLogAdapter adapter = new GameLogAdapter(LayoutInflater.from(requireContext()));
        recyclerView.setAdapter(adapter);

        GameViewModel model = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
        model.getLogs().observe(this, logLines -> {
            adapter.update(logLines);
            if (!logLines.isEmpty())
                recyclerView.scrollToPosition(logLines.size() - 1);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log, container, false);
    }
}