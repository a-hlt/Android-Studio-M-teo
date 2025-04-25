package com.example.meteoapp; // Adaptez à votre package

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.util.Log;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.ExistingWorkPolicy;

import com.example.meteoapp.data.WeatherData; // Adaptez à votre package


public class WeatherWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "WeatherWidgetProvider";
    private static final String UNIQUE_WORK_NAME = "WidgetUpdateWork";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate appelé - Planification du Worker");
        scheduleWidgetUpdate(context);
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "onEnabled appelé - Planification du Worker initial");
        scheduleWidgetUpdate(context);
    }

    private void scheduleWidgetUpdate(Context context) {
        OneTimeWorkRequest updateWorkRequest =
                new OneTimeWorkRequest.Builder(WidgetUpdateWorker.class)
                        .build();

        WorkManager.getInstance(context).enqueueUniqueWork(
                UNIQUE_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                updateWorkRequest);

        Log.d(TAG, "WidgetUpdateWorker planifié.");
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, WeatherData weatherData, String displayCity) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        Log.d(TAG, "Mise à jour de l'UI pour widget ID: " + appWidgetId);

        if (weatherData != null && weatherData.getMain() != null) {
            String temp = String.format("%.0f°C", weatherData.getMain().getTemp());
            views.setTextViewText(R.id.widget_city, weatherData.getName());
            views.setTextViewText(R.id.widget_temp, temp);

            if (weatherData.getWeather() != null && !weatherData.getWeather().isEmpty()) {
                views.setImageViewResource(R.id.widget_icon, getWeatherIconResource(weatherData.getWeather().get(0).getMain()));
            } else {
                views.setImageViewResource(R.id.widget_icon, R.mipmap.ic_launcher);
            }

        } else {
            views.setTextViewText(R.id.widget_city, displayCity);
            views.setTextViewText(R.id.widget_temp, "Erreur");
            views.setImageViewResource(R.id.widget_icon, R.mipmap.ic_launcher);
            Log.w(TAG, "WeatherData est null pour le widget " + appWidgetId);
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
        Log.d(TAG, "AppWidgetManager.updateAppWidget appelé pour " + appWidgetId);
    }

    private static int getWeatherIconResource(String weatherMain) {
        if (weatherMain == null) return R.mipmap.ic_launcher;

        weatherMain = weatherMain.toLowerCase();

        if (weatherMain.contains("clear")) {
            return R.drawable.ic_clear;
        } else if (weatherMain.contains("clouds")) {
            return R.drawable.ic_clouds;
        } else if (weatherMain.contains("rain") || weatherMain.contains("drizzle")) {
            return R.drawable.ic_rain;
        } else if (weatherMain.contains("thunderstorm")) {
            return R.drawable.ic_thunderstorm;
        } else if (weatherMain.contains("snow")) {
            return R.drawable.ic_snow;
        } else if (weatherMain.contains("mist") || weatherMain.contains("fog") || weatherMain.contains("haze")) {
            return R.drawable.ic_mist;
        }
        return R.mipmap.ic_launcher; // Adaptez l'icône par défaut
    }


    @Override
    public void onDisabled(Context context) {
        Log.d(TAG, "onDisabled appelé - Annulation du Worker planifié");
        WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_WORK_NAME);
    }
}