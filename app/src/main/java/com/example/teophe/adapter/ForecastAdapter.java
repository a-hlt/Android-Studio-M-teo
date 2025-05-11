package com.example.teophe.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teophe.R;
import com.example.teophe.model.Forecast;
import com.example.teophe.utils.WeatherIconMapper;

import java.util.List;
import java.util.Locale;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    private List<Forecast> forecastList;

    public ForecastAdapter(List<Forecast> forecastList) {
        this.forecastList = forecastList;
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forecast, parent, false);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        Forecast forecast = forecastList.get(position);
        holder.tvDay.setText(forecast.getDayName());
        holder.tvDate.setText(forecast.getDate());
        holder.ivWeatherIcon.setImageResource(WeatherIconMapper.getIconResourceFromApiId(forecast.getIconIdApi()));
        holder.tvDescription.setText(forecast.getDescription());
        holder.tvTempMax.setText(String.format(Locale.getDefault(), "%.0f°", forecast.getTempMax()));
        holder.tvTempMin.setText(String.format(Locale.getDefault(), "%.0f°", forecast.getTempMin()));
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    static class ForecastViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay, tvDate, tvDescription, tvTempMax, tvTempMin;
        ImageView ivWeatherIcon;

        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivWeatherIcon = itemView.findViewById(R.id.ivWeatherIcon);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTempMax = itemView.findViewById(R.id.tvTempMax);
            tvTempMin = itemView.findViewById(R.id.tvTempMin);
        }
    }
}