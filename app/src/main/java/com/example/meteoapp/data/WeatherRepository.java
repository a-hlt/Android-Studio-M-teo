package com.example.meteoapp.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.meteoapp.network.ApiService;
import com.example.meteoapp.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherRepository {

    private ApiService apiService;
    private String apiKey = "2364a112c01388903d79d828efca398a"; // !! METTEZ VOTRE CLÉ ICI !!

    private MutableLiveData<WeatherData> weatherData = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();


    public WeatherRepository() {
        apiService = RetrofitClient.getApiService();
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

    public void fetchWeatherByCity(String city) {
        isLoading.setValue(true);
        error.setValue(null);
        apiService.getWeatherByCity(city, apiKey, "metric", "fr").enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    weatherData.setValue(response.body());
                } else {
                    handleApiError(response);
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Erreur réseau: " + t.getMessage());
            }
        });
    }

    public void fetchWeatherByCoordinates(double lat, double lon) {
        isLoading.setValue(true);
        error.setValue(null);
        apiService.getWeatherByCoords(lat, lon, apiKey, "metric", "fr").enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    weatherData.setValue(response.body());
                } else {
                    handleApiError(response);
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Erreur réseau: " + t.getMessage());
            }
        });
    }

    private void handleApiError(Response<?> response) {
        String errorMessage = "Erreur inconnue";
        if (response.code() == 404) {
            errorMessage = "Ville non trouvée";
        } else if (response.code() == 401) {
            errorMessage = "Clé API invalide ou manquante";
        } else if (response.errorBody() != null) {
            try {
                errorMessage = "Erreur " + response.code() + ": " + response.errorBody().string();
            } catch (Exception e) {
                errorMessage = "Erreur serveur: " + response.code();
            }
        } else {
            errorMessage = "Erreur serveur: " + response.code();
        }
        error.setValue(errorMessage);
    }

}