package com.example.chess;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater layoutInflater;
    private final List<Message> data = new ArrayList<>();

    void updateData(List<Message> messages) {
        if (messages.isEmpty()) {
            data.clear();
            notifyDataSetChanged();
        } else {
            Message message = messages.get(messages.size() - 1);
            data.add(message);
            notifyItemInserted(data.size() - 1);
        }
    }

    public ChatAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    private static class OurMessageViewHolder extends RecyclerView.ViewHolder {
        public OurMessageViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private static class NetworkMessageViewHolder extends RecyclerView.ViewHolder {
        public NetworkMessageViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View v = layoutInflater.inflate(R.layout.item_message_received, parent, false);
            return new NetworkMessageViewHolder(v);
        } else {
            View v = layoutInflater.inflate(R.layout.item_message_sent, parent, false);
            return new OurMessageViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TextView textView = holder.itemView.findViewById(R.id.text_message_body);
        textView.setText(data.get(position).getText());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).isOur() ? 2 : 1;
    }
}
