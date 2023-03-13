package ru.oceancraft.chess.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.oceancraft.chess.R;
import ru.oceancraft.chess.model.LogLine;

public class GameLogAdapter extends RecyclerView.Adapter<GameLogAdapter.GameLogViewHolder> {
    private final List<LogLine> listOfLog = new ArrayList<>();
    private final LayoutInflater layoutInflater;

    public GameLogAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    public void update(List<LogLine> logLines) {
        if (logLines.isEmpty()) {
            listOfLog.clear();
        } else {
            listOfLog.clear();
            listOfLog.addAll(logLines);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GameLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.log_line, parent, false);
        return new GameLogViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GameLogViewHolder holder, int position) {
        holder.textView.setText(getLogs(position));
    }

    private String getLogs(int position) {
        StringBuilder sb = new StringBuilder();
        int moveNumber = (position / 2) + 1;
        if (position % 2 == 0) {
            sb.append(moveNumber);
            sb.append(". ");
        } else {
            int spaceCount = (int) (Math.log10(moveNumber) + 1) + 2;
            char[] spaces = new char[spaceCount];
            Arrays.fill(spaces, ' ');
            sb.append(spaces);
        }
        sb.append(listOfLog.get(position).toString());

        return sb.toString();
    }

    @Override
    public int getItemCount() {
        return listOfLog.size();
    }

    static class GameLogViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public GameLogViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.log_line);
        }
    }
}
