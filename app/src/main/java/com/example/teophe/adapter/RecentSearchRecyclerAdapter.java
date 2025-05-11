package com.example.teophe.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.teophe.R;
import java.util.List;

public class RecentSearchRecyclerAdapter extends RecyclerView.Adapter<RecentSearchRecyclerAdapter.ViewHolder> {

    private List<String> recentSearchItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String cityName);
    }

    public RecentSearchRecyclerAdapter(List<String> items, OnItemClickListener listener) {
        this.recentSearchItems = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_search_dark, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String cityName = recentSearchItems.get(position);
        holder.tvCityName.setText(cityName);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(cityName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recentSearchItems.size();
    }

    public void updateData(List<String> newItems) {
        this.recentSearchItems.clear();
        this.recentSearchItems.addAll(newItems);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItemRecentIcon;
        TextView tvCityName;

        ViewHolder(View itemView) {
            super(itemView);
            tvCityName = itemView.findViewById(R.id.tvItemRecentCityName);
        }
    }
}