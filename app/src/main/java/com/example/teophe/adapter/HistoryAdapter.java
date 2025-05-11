package com.example.teophe.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teophe.R;
import com.example.teophe.model.WeatherData;
import com.example.teophe.utils.WeatherIconMapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<WeatherData> historyList;
    private OnHistoryItemClickListener listener;

    public interface OnHistoryItemClickListener {
        void onHistoryItemClick(WeatherData weatherData);
        void onHistoryItemLongClick(WeatherData weatherData, View view);
    }

    public HistoryAdapter(List<WeatherData> historyList, OnHistoryItemClickListener listener) {
        this.historyList = historyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        WeatherData weatherData = historyList.get(position);
        holder.tvCityName.setText(weatherData.getCityName());

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault());
        holder.tvDateTime.setText(sdf.format(new Date(weatherData.getTimestamp())));


        String temperatureUnit = "°C";
        holder.tvTemperature.setText(String.format(Locale.getDefault(), "%.0f%s", weatherData.getTemperature(), temperatureUnit));
        holder.ivWeatherIcon.setImageResource(WeatherIconMapper.getIconResourceFromApiId(weatherData.getIconIdApi()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHistoryItemClick(weatherData);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onHistoryItemLongClick(weatherData, v);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivWeatherIcon;
        TextView tvCityName, tvDateTime, tvTemperature;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivWeatherIcon = itemView.findViewById(R.id.ivWeatherIcon);
            tvCityName = itemView.findViewById(R.id.tvCityName);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvTemperature = itemView.findViewById(R.id.tvTemperature);
        }
    }
}