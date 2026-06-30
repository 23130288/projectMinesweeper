package com.example.project.game;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project.R;
import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import Model.LeaderBoard;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private final List<LeaderBoard> list;
    private OnItemClickListener clickListener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

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
        holder.tvPlayerName.setText(item.getPlayerName());
        holder.tvScore.setText(String.format(Locale.getDefault(), "%d Pts", item.getScore()));
        holder.tvTime.setText(String.format(Locale.getDefault(), "⏱️ %02d:%02d", item.getCompletedTime() / 60, item.getCompletedTime() % 60));

        // Xử lý hiển thị ngày giờ hoàn thành kỷ lục
        Timestamp timestamp = item.getCompletedAt();
        if (timestamp != null) {
            holder.tvCompletedAt.setVisibility(View.VISIBLE);
            holder.tvCompletedAt.setText(dateFormat.format(timestamp.toDate()));
        } else {
            holder.tvCompletedAt.setVisibility(View.GONE);
        }

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
        final TextView tvRank, tvPlayerName, tvScore, tvTime, tvCompletedAt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            tvPlayerName = itemView.findViewById(R.id.tvPlayerName);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvCompletedAt = itemView.findViewById(R.id.tvCompletedAt); // Ánh xạ TextView mới
        }
    }
}