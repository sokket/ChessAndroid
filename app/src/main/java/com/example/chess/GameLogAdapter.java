package com.example.chess;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chess.game.LogLine;

import java.util.ArrayList;
import java.util.List;

public class GameLogAdapter extends RecyclerView.Adapter<GameLogAdapter.GameLogViewHolder> {
    List<LogLine> listOfLog = new ArrayList<>();
    final LayoutInflater layoutInflater;

    public GameLogAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    public void addLine(LogLine logLine) {
        listOfLog.add(logLine);
        notifyItemInserted(listOfLog.size() - 1);
    }

    public void clean() {
        listOfLog.clear();
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
