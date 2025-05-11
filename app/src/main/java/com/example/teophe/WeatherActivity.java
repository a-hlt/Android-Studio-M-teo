package com.example.teophe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.teophe.adapter.ForecastAdapter;
import com.example.teophe.database.DatabaseHelper;
import com.example.teophe.model.Forecast;
import com.example.teophe.model.WeatherData;
import com.example.teophe.model.WeatherPreferences;
import com.example.teophe.service.WeatherService;
import com.example.teophe.utils.WeatherIconMapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeatherActivity extends AppCompatActivity {

    private static final String API_KEY = "2364a112c01388903d79d828efca398a";

    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageButton btnBack;
    private TextView tvCityName;
    private TextView tvDateTime;
    private ImageView ivWeatherIcon;
    private TextView tvTemperature;
    private TextView tvWeatherDescription;
    private TextView tvFeelsLike;
    private TextView tvHumidity;
    private TextView tvWind;
    private TextView tvPressure;
    private TextView tvSunrise;
    private TextView tvSunset;
    private RecyclerView rvForecast;

    private String cityName;
    private double latitude;
    private double longitude;
    private boolean searchByCoordinates = false;

    private DatabaseHelper databaseHelper;
    private WeatherService weatherService;
    private ForecastAdapter forecastAdapter;
    private List<Forecast> forecastList;

    private WeatherPreferences weatherPreferences;
    private String currentUnits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        initViews();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        weatherPreferences = new WeatherPreferences(sharedPreferences);
        currentUnits = weatherPreferences.getUnits();

        databaseHelper = new DatabaseHelper(this);

        weatherService = new WeatherService(API_KEY);

        forecastList = new ArrayList<>();
        forecastAdapter = new ForecastAdapter(forecastList);
        rvForecast.setAdapter(forecastAdapter);
        rvForecast.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        getSearchParameters();

        swipeRefreshLayout.setOnRefreshListener(this::refreshWeatherData);

        loadWeatherData();

        btnBack.setOnClickListener(v -> onBackPressed());
    }

    private void initViews() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        btnBack = findViewById(R.id.btnBack);
        tvCityName = findViewById(R.id.tvCityName);
        tvDateTime = findViewById(R.id.tvDateTime);
        ivWeatherIcon = findViewById(R.id.ivWeatherIcon);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvWeatherDescription = findViewById(R.id.tvWeatherDescription);
        tvFeelsLike = findViewById(R.id.tvFeelsLike);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvWind = findViewById(R.id.tvWind);
        tvPressure = findViewById(R.id.tvPressure);
        tvSunrise = findViewById(R.id.tvSunrise);
        tvSunset = findViewById(R.id.tvSunset);
        rvForecast = findViewById(R.id.rvForecast);
    }

    private void getSearchParameters() {
        Intent intent = getIntent();
        if (intent.hasExtra("CITY_NAME")) {
            cityName = intent.getStringExtra("CITY_NAME");
            searchByCoordinates = false;
        } else if (intent.hasExtra("LATITUDE") && intent.hasExtra("LONGITUDE")) {
            latitude = intent.getDoubleExtra("LATITUDE", 0);
            longitude = intent.getDoubleExtra("LONGITUDE", 0);
            searchByCoordinates = true;
        } else {
            cityName = weatherPreferences.getLastSearchedCity();
            if (cityName == null || cityName.isEmpty()){
                cityName = "Paris";
                Toast.makeText(this, R.string.default_city_fallback, Toast.LENGTH_SHORT).show();
            }
            searchByCoordinates = false;
        }
    }

    private void loadWeatherData() {
        swipeRefreshLayout.setRefreshing(true);
        if (searchByCoordinates) {
            weatherService.getWeatherByCoordinates(latitude, longitude, currentUnits, weatherDataListener);
        } else {
            weatherService.getWeatherByCity(cityName, currentUnits, weatherDataListener);
        }
    }

    private void refreshWeatherData() {
        currentUnits = weatherPreferences.getUnits();
        loadWeatherData();
    }

    private final WeatherService.WeatherDataListener weatherDataListener = new WeatherService.WeatherDataListener() {
        @Override
        public void onSuccess(WeatherData weatherData) {
            swipeRefreshLayout.setRefreshing(false);
            updateWeatherUI(weatherData);
            loadForecastData(weatherData.getLatitude(), weatherData.getLongitude());
            saveSearchToDatabase(weatherData);

            weatherPreferences.setLastSearchedCity(weatherData.getCityName());

            if (searchByCoordinates && weatherData.getCityName() != null) {
                cityName = weatherData.getCityName();
                searchByCoordinates = false;
            }
        }

        @Override
        public void onError(String message) {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(WeatherActivity.this, getString(R.string.error_fetching_weather, message), Toast.LENGTH_LONG).show();
        }
    };

    private final WeatherService.ForecastListener forecastListener = new WeatherService.ForecastListener() {
        @Override
        public void onSuccess(List<Forecast> forecasts) {
            forecastList.clear();
            forecastList.addAll(forecasts);
            forecastAdapter.notifyDataSetChanged();
        }

        @Override
        public void onError(String message) {
            Toast.makeText(WeatherActivity.this, getString(R.string.error_fetching_forecast, message), Toast.LENGTH_SHORT).show();
        }
    };


    private void updateWeatherUI(WeatherData weatherData) {
        tvCityName.setText(weatherData.getCityName());

        String dateTime = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).format(new Date(weatherData.getTimestamp()));
        tvDateTime.setText(dateTime);

        ivWeatherIcon.setImageResource(WeatherIconMapper.getIconResourceFromApiId(weatherData.getIconIdApi()));

        String tempUnitSuffix = currentUnits.equals("metric") ? "°C" : "°F";
        tvTemperature.setText(String.format(Locale.getDefault(), "%.0f%s", weatherData.getTemperature(), tempUnitSuffix));
        tvFeelsLike.setText(String.format(Locale.getDefault(), "%.0f%s", weatherData.getFeelsLike(), tempUnitSuffix));

        tvWeatherDescription.setText(weatherData.getDescription());

        tvHumidity.setText(String.format(Locale.getDefault(), "%d%%", weatherData.getHumidity()));

        double windSpeedDisplay = weatherData.getWindSpeed();
        String windUnitSuffix;
        if (currentUnits.equals("metric")) {
            windSpeedDisplay = weatherData.getWindSpeed() * 3.6;
            windUnitSuffix = " km/h";
        } else {
            windUnitSuffix = " mph";
        }
        tvWind.setText(String.format(Locale.getDefault(), "%.1f %s", windSpeedDisplay, windUnitSuffix));

        tvPressure.setText(String.format(Locale.getDefault(), "%d hPa", weatherData.getPressure()));

        tvSunrise.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(weatherData.getSunrise() * 1000L)));
        tvSunset.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(weatherData.getSunset() * 1000L)));
    }

    private void loadForecastData(double lat, double lon) {
        weatherService.getForecast(lat, lon, currentUnits, forecastListener);
    }


    private void saveSearchToDatabase(WeatherData weatherData) {
        databaseHelper.insertWeatherData(weatherData);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}