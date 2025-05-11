package com.example.teophe.utils;

import com.example.teophe.R;

public class WeatherIconMapper {

    public static int getIconResourceFromApiId(String apiIconId) {
        if (apiIconId == null) return R.drawable.ic_weather_default;

        switch (apiIconId) {
            case "01d":
                return R.drawable.ic_weather_sunny;
            case "01n":
                return R.drawable.ic_weather_clear_night;
            case "02d":
                return R.drawable.ic_weather_few_clouds_day;
            case "02n":
                return R.drawable.ic_weather_few_clouds_night;
            case "03d":
            case "03n":
                return R.drawable.ic_weather_scattered_clouds;
            case "04d":
            case "04n":
                return R.drawable.ic_weather_broken_clouds;
            case "09d":
            case "09n":
                return R.drawable.ic_weather_shower_rain;
            case "10d":
                return R.drawable.ic_weather_rain_day;
            case "10n":
                return R.drawable.ic_weather_rain_night;
            case "11d":
            case "11n":
                return R.drawable.ic_weather_thunderstorm;
            case "13d":
            case "13n":
                return R.drawable.ic_weather_snow;
            case "50d":
            case "50n":
                return R.drawable.ic_weather_mist;
            default:
                return R.drawable.ic_weather_default;
        }
    }

    public static int getIconResourceForWeatherCode(int weatherCode) {
        if (weatherCode >= 200 && weatherCode < 300) {
            return R.drawable.ic_weather_thunderstorm;
        } else if (weatherCode >= 300 && weatherCode < 400) {
            return R.drawable.ic_weather_shower_rain;
        } else if (weatherCode >= 500 && weatherCode < 600) {
            if (weatherCode == 500 || weatherCode == 501) return R.drawable.ic_weather_rain_day;
            if (weatherCode == 511) return R.drawable.ic_weather_snow;
            return R.drawable.ic_weather_rain_day;
        } else if (weatherCode >= 600 && weatherCode < 700) {
            return R.drawable.ic_weather_snow;
        } else if (weatherCode >= 700 && weatherCode < 800) {
            return R.drawable.ic_weather_mist;
        } else if (weatherCode == 800) {
            return R.drawable.ic_weather_sunny;
        } else if (weatherCode == 801) {
            return R.drawable.ic_weather_few_clouds_day;
        } else if (weatherCode == 802) {
            return R.drawable.ic_weather_scattered_clouds;
        } else if (weatherCode > 802 && weatherCode < 805) {
            return R.drawable.ic_weather_broken_clouds;
        }
        return R.drawable.ic_weather_default;
    }
}