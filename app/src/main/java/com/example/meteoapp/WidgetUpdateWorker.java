package com.example.meteoapp;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.meteoapp.data.WeatherData;
import com.example.meteoapp.network.ApiService;
import com.example.meteoapp.network.RetrofitClient;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Response;

public class WidgetUpdateWorker extends Worker {

    private static final String TAG = "WidgetUpdateWorker";
    private static final String API_KEY = "2364a112c01388903d79d828efca398a"; 

    public WidgetUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "WidgetUpdateWorker démarré.");
        Context context = getApplicationContext();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, WeatherWidgetProvider.class));

        String city = "Paris"; // Exemple, à remplacer par une préférence si nécessaire

        WeatherData weatherData = fetchWeatherDataSync(city);

        for (int appWidgetId : appWidgetIds) {
            WeatherWidgetProvider.updateAppWidget(context, appWidgetManager, appWidgetId, weatherData, city);
        }

        Log.d(TAG, "WidgetUpdateWorker terminé.");
        return (weatherData != null) ? Result.success() : Result.failure();
    }

    private WeatherData fetchWeatherDataSync(String city) {
        ApiService service = RetrofitClient.getApiService();
        Call<WeatherData> call = service.getWeatherByCity(city, API_KEY, "metric", "fr");
        try {
            Response<WeatherData> response = call.execute();
            if (response.isSuccessful()) {
                Log.d(TAG, "Données météo récupérées pour " + city);
                return response.body();
            } else {
                Log.e(TAG, "Erreur API Worker: " + response.code());
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, "Erreur réseau Worker: " + e.getMessage());
            return null;
        }
    }
}