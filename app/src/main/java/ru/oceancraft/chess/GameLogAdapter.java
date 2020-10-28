package ru.oceancraft.chess;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oceancraft.chess.R;

import ru.oceancraft.chess.game.LogLine;

import java.util.ArrayList;
import java.util.List;

public class GameLogAdapter extends RecyclerView.Adapter<GameLogAdapter.GameLogViewHolder> {
    private final List<LogLine> listOfLog = new ArrayList<>();
    private final LayoutInflater layoutInflater;

    public GameLogAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    public void update(List<LogLine> logLines) {
        if (logLines.isEmpty()) {
            listOfLog.clear();
            notifyDataSetChanged();
        } else {
            LogLine last = logLines.get(logLines.size() - 1);
            listOfLog.add(last);
            notifyItemInserted(listOfLog.size() - 1);
        }
    }

    @NonNull
    @Override
    public GameLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.log_line, parent, false);
        return new GameLogViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GameLogViewHolder holder, int position) {
        String log = position + 1 + ". " + listOfLog.get(position).toString();
        holder.textView.setText(log);
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
