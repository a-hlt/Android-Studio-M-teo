package com.example.teophe.model;

import android.content.SharedPreferences;

public class WeatherPreferences {
    private SharedPreferences sharedPreferences;

    public static final String PREF_KEY_UNITS = "pref_units";
    public static final String PREF_KEY_LAST_CITY = "pref_last_city";
    public static final String DEFAULT_UNITS = "metric";

    public WeatherPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public String getUnits() {
        return sharedPreferences.getString(PREF_KEY_UNITS, DEFAULT_UNITS);
    }

    public void setUnits(String units) {
        sharedPreferences.edit().putString(PREF_KEY_UNITS, units).apply();
    }

    public String getLastSearchedCity() {
        return sharedPreferences.getString(PREF_KEY_LAST_CITY, "");
    }

    public void setLastSearchedCity(String city) {
        sharedPreferences.edit().putString(PREF_KEY_LAST_CITY, city).apply();
    }
}