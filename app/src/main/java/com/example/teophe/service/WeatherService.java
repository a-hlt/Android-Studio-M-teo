package com.example.teophe.service;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.teophe.model.Forecast;
import com.example.teophe.model.WeatherData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherService {
    private static final String TAG = "WeatherService";
    private final String apiKey;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    public interface WeatherDataListener {
        void onSuccess(WeatherData weatherData);
        void onError(String message);
    }

    public interface ForecastListener {
        void onSuccess(List<Forecast> forecasts);
        void onError(String message);
    }

    public WeatherService(String apiKey) {
        this.apiKey = apiKey;
    }

    public void getWeatherByCity(String cityName, String units, WeatherDataListener listener) {
        String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + apiKey + "&units=" + units + "&lang=fr";
        fetchWeatherData(urlString, listener);
    }

    public void getWeatherByCoordinates(double lat, double lon, String units, WeatherDataListener listener) {
        String urlString = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey + "&units=" + units + "&lang=fr";
        fetchWeatherData(urlString, listener);
    }

    private void fetchWeatherData(String urlString, WeatherDataListener listener) {
        executorService.execute(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    WeatherData weatherData = parseWeatherData(response.toString());
                    mainThreadHandler.post(() -> listener.onSuccess(weatherData));
                } else {
                    Log.e(TAG, "HTTP Error: " + responseCode + " for URL: " + urlString);
                    String errorMessage = "Erreur: " + connection.getResponseMessage() + " (" + responseCode + ")";
                    try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                        StringBuilder errorResponse = new StringBuilder();
                        String line;
                        while ((line = errorReader.readLine()) != null) {
                            errorResponse.append(line);
                        }
                        JSONObject errorJson = new JSONObject(errorResponse.toString());
                        if (errorJson.has("message")) {
                            errorMessage = errorJson.getString("message");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error stream", e);
                    }
                    final String finalErrorMessage = errorMessage;
                    mainThreadHandler.post(() -> listener.onError(finalErrorMessage));
                }
                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Exception in fetchWeatherData: ", e);
                mainThreadHandler.post(() -> listener.onError("Erreur de connexion: " + e.getMessage()));
            }
        });
    }

    private WeatherData parseWeatherData(String jsonResponse) throws Exception {
        JSONObject jsonObj = new JSONObject(jsonResponse);

        JSONObject main = jsonObj.getJSONObject("main");
        JSONObject wind = jsonObj.getJSONObject("wind");
        JSONObject sys = jsonObj.getJSONObject("sys");
        JSONObject coord = jsonObj.getJSONObject("coord");
        JSONArray weatherArray = jsonObj.getJSONArray("weather");
        JSONObject weather = weatherArray.getJSONObject(0);

        String cityName = jsonObj.getString("name");
        double temp = main.getDouble("temp");
        double feelsLike = main.getDouble("feels_like");
        int humidity = main.getInt("humidity");
        int pressure = main.getInt("pressure");
        double windSpeed = wind.getDouble("speed");
        String description = weather.getString("description");
        int weatherCode = weather.getInt("id");
        String iconIdApi = weather.getString("icon");
        double lat = coord.getDouble("lat");
        double lon = coord.getDouble("lon");
        long sunrise = sys.getLong("sunrise");
        long sunset = sys.getLong("sunset");
        long dt = jsonObj.getLong("dt");

        return new WeatherData(cityName, temp, feelsLike, humidity, pressure, windSpeed,
                capitalizeFirstLetter(description), weatherCode, iconIdApi, lat, lon, sunrise, sunset, dt * 1000);
    }

    private String capitalizeFirstLetter(String original) {
        if (original == null || original.isEmpty()) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }


    public void getForecast(double lat, double lon, String units, ForecastListener listener) {
        String urlString = "https://api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey + "&units=" + units + "&lang=fr";
        fetchForecastData(urlString, listener);
    }

    private void fetchForecastData(String urlString, ForecastListener listener) {
        executorService.execute(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    List<Forecast> forecasts = parseForecastData(response.toString());
                    mainThreadHandler.post(() -> listener.onSuccess(forecasts));
                } else {
                    Log.e(TAG, "HTTP Error for Forecast: " + connection.getResponseCode() + " for URL: " + urlString);
                    String errorMessage = "Erreur: " + connection.getResponseMessage() + " (" + connection.getResponseCode() + ")";
                    final String finalErrorMessage = errorMessage;
                    mainThreadHandler.post(() -> listener.onError(finalErrorMessage));
                }
                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Exception in fetchForecastData: ", e);
                mainThreadHandler.post(() -> listener.onError("Erreur de connexion pour les prévisions: " + e.getMessage()));
            }
        });
    }

    private List<Forecast> parseForecastData(String jsonResponse) throws Exception {
        List<Forecast> dailyForecasts = new ArrayList<>();
        JSONObject jsonObj = new JSONObject(jsonResponse);
        JSONArray list = jsonObj.getJSONArray("list");

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.FRENCH);
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM", Locale.FRENCH);

        String previousDay = "";

        for (int i = 0; i < list.length(); i++) {
            JSONObject item = list.getJSONObject(i);
            long dt = item.getLong("dt");
            Date date = new Date(dt * 1000);

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            String currentDayName = dayFormat.format(date);
            if (!currentDayName.equals(previousDay) && dailyForecasts.size() < 5) {

                JSONObject main = item.getJSONObject("main");
                JSONArray weatherArray = item.getJSONArray("weather");
                JSONObject weather = weatherArray.getJSONObject(0);

                String dayNameStr = capitalizeFirstLetter(currentDayName);
                String dateStr = dateFormat.format(date);
                String iconIdApi = weather.getString("icon");
                String description = capitalizeFirstLetter(weather.getString("description"));
                double tempMax = main.getDouble("temp_max");
                double tempMin = main.getDouble("temp_min");

                dailyForecasts.add(new Forecast(dayNameStr, dateStr, iconIdApi, description, tempMax, tempMin));
                previousDay = currentDayName;
            }
            if (dailyForecasts.size() >= 5) break;
        }
        return dailyForecasts;
    }
}