package com.example.project.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project.R;
import java.util.List;
import Model.LeaderBoard;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private final List<LeaderBoard> list;
    private OnItemClickListener clickListener;
    public interface OnItemClickListener {
        void onItemClick(LeaderBoard item);
    }
    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public LeaderboardAdapter(List<LeaderBoard> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeaderBoard item = list.get(position);
        int rank = position + 1;

        holder.tvRank.setText(String.valueOf(rank));
        holder.tvPlayerId.setText(item.getPlayerName());
        holder.tvScore.setText(String.format("%d Pts", item.getScore()));
        holder.tvTime.setText(String.format("⏱️ %02d:%02d", item.getCompletedTime() / 60, item.getCompletedTime() % 60));

        if (holder.tvRank.getBackground() instanceof GradientDrawable) {
            GradientDrawable rankBackground = (GradientDrawable) holder.tvRank.getBackground();
            switch (rank) {
                case 1 -> rankBackground.setColor(Color.parseColor("#F9AB00"));
                case 2 -> rankBackground.setColor(Color.parseColor("#9AA0A6"));
                case 3 -> rankBackground.setColor(Color.parseColor("#DE8544"));
                default -> rankBackground.setColor(Color.parseColor("#757575"));
            }
        }
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null && position != RecyclerView.NO_POSITION) {
                clickListener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvRank, tvPlayerId, tvScore, tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            tvPlayerId = itemView.findViewById(R.id.tvPlayerId);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}