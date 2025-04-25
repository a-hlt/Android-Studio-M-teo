package com.example.meteoapp.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.meteoapp.R;
import com.example.meteoapp.data.ForecastItem; // Assurez-vous que ce modèle existe et est correct

import java.util.ArrayList;
import java.util.List;


public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    private List<ForecastItem> forecastList = new ArrayList<>();
    private Context context;

    public ForecastAdapter(Context context) {
        this.context = context;
    }

    public void setForecastList(List<ForecastItem> forecastList) {
        this.forecastList = (forecastList != null) ? forecastList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_forecast, parent, false);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        ForecastItem item = forecastList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    class ForecastViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate;
        ImageView imageViewIcon;
        TextView textViewTemp;

        ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewForecastDate);
            imageViewIcon = itemView.findViewById(R.id.imageViewForecastIcon);
            textViewTemp = itemView.findViewById(R.id.textViewForecastTemp);
        }

        void bind(ForecastItem item) {
            // Adaptez selon votre modèle ForecastItem et les méthodes de formatage/chargement d'icône
            // textViewDate.setText(formatDate(item.getTimestamp()));
            if (item.getMain() != null) { // Vérification ajoutée
                textViewTemp.setText(String.format("%.0f°C", item.getMain().getTemp()));
            } else {
                textViewTemp.setText("N/A");
            }
            // loadWeatherIcon(item.getWeather().get(0).getIcon(), imageViewIcon);
        }
    }
}