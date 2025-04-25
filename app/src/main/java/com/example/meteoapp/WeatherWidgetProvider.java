package com.example.meteoapp;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class WeatherWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "WeatherWidgetProvider";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate called");
        startWidgetUpdateService(context);
    }

    private void startWidgetUpdateService(Context context) {
        Intent intent = new Intent(context, WidgetUpdateService.class);
        intent.setAction(WidgetUpdateService.ACTION_UPDATE_WIDGET);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent); // Utiliser startForegroundService pour O+
        } else {
            context.startService(intent);
        }
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "onEnabled called - Starting initial update");
        startWidgetUpdateService(context);
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(TAG, "onDisabled called - Stopping service (if applicable)");
    }
}