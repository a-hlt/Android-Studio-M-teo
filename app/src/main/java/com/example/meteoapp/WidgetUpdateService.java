package com.example.meteoapp;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.util.Log;
import com.example.meteoapp.data.WeatherData;
import com.example.meteoapp.network.ApiService;
import com.example.meteoapp.network.RetrofitClient;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Response;

public class WidgetUpdateService extends IntentService {

    private static final String TAG = "WidgetUpdateService";
    public static final String ACTION_UPDATE_WIDGET = "com.votredomaine.votremeteoapp.action.UPDATE_WIDGET";
    private static final String API_KEY = "2364a112c01388903d79d828efca398a";


    public WidgetUpdateService() {
        super("WidgetUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null && ACTION_UPDATE_WIDGET.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, WeatherWidgetProvider.class));

            String city = "Toulouse";

            WeatherData weatherData = fetchWeatherDataSync(city);

            for (int appWidgetId : appWidgetIds) {
                updateAppWidget(this, appWidgetManager, appWidgetId, weatherData, city);
            }
        }
    }

    private WeatherData fetchWeatherDataSync(String city) {
        ApiService service = RetrofitClient.getApiService();
        Call<WeatherData> call = service.getWeatherByCity(city, API_KEY, "metric", "fr");
        try {
            Response<WeatherData> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                Log.e(TAG, "Erreur API Widget: " + response.code());
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, "Erreur réseau Widget: " + e.getMessage());
            return null;
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, WeatherData weatherData, String displayCity) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        if (weatherData != null && weatherData.getMain() != null) {
            String temp = String.format("%.0f°C", weatherData.getMain().getTemp());
            views.setTextViewText(R.id.widget_city, weatherData.getName());
            views.setTextViewText(R.id.widget_temp, temp);
            views.setImageViewResource(R.id.widget_icon, R.mipmap.ic_launcher);

        } else {
            views.setTextViewText(R.id.widget_city, displayCity);
            views.setTextViewText(R.id.widget_temp, "Erreur");
            views.setImageViewResource(R.id.widget_icon, R.mipmap.ic_launcher);
        }

        Intent refreshIntent = new Intent(context, WeatherWidgetProvider.class);
        refreshIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{ appWidgetId });

        PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context,
                appWidgetId,
                refreshIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        views.setOnClickPendingIntent(R.id.widget_refresh_button, refreshPendingIntent);

        Intent openAppIntent = new Intent(context, MainActivity.class);
        PendingIntent openAppPendingIntent = PendingIntent.getActivity(context,
                0,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_icon, openAppPendingIntent);
        views.setOnClickPendingIntent(R.id.widget_city, openAppPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}