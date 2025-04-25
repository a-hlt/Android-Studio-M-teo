package com.example.meteoapp.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.meteoapp.data.WeatherData;
import com.example.meteoapp.data.WeatherRepository;
// Importez ForecastItem si vous l'utilisez
// import com.example.meteoapp.data.ForecastItem;
// import java.util.List;

public class WeatherViewModel extends ViewModel {

    private WeatherRepository repository;
    private LiveData<WeatherData> weatherData;
    private LiveData<String> error;
    private LiveData<Boolean> isLoading;
    // private LiveData<List<ForecastItem>> forecastData;


    public WeatherViewModel() {
        repository = new WeatherRepository();
        weatherData = repository.getWeatherData();
        error = repository.getError();
        isLoading = repository.getIsLoading();
        // forecastData = repository.getForecastData(); // Si implémenté
    }

    public LiveData<WeatherData> getWeatherData() {
        return weatherData;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /* Si implémenté
    public LiveData<List<ForecastItem>> getForecastData() {
        return forecastData;
    }
    */

    public void fetchWeatherByCity(String city) {
        repository.fetchWeatherByCity(city);
    }

    public void fetchWeatherByCoordinates(double lat, double lon) {
        repository.fetchWeatherByCoordinates(lat, lon);
    }

    /* Si implémenté
    public void fetchForecast(String cityOrLatLon) {
        // repository.fetchForecast(...);
    }
    */

    // public void clearError() { repository.clearError(); }
}