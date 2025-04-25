package com.example.meteoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.example.meteoapp.data.ForecastItem; // Assurez-vous que ce chemin est correct
import com.example.meteoapp.data.WeatherData;
import com.example.meteoapp.ui.CustomWeatherIconView; // Assurez-vous que ce chemin est correct
import com.example.meteoapp.ui.ForecastAdapter;    // Assurez-vous que ce chemin est correct
import com.example.meteoapp.ui.WeatherViewModel;   // Assurez-vous que ce chemin est correct

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private EditText editTextCity;
    private Button buttonSearch;
    private Button buttonGps;
    private TextView textViewCity;
    private TextView textViewTemperature;
    private TextView textViewDescription;
    private ImageView imageViewWeatherIcon;
    private CustomWeatherIconView customIconView;
    private ProgressBar progressBarLoading;
    private RecyclerView recyclerViewForecast;

    private WeatherViewModel weatherViewModel;
    private ForecastAdapter forecastAdapter;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextCity = findViewById(R.id.editTextCity);
        buttonSearch = findViewById(R.id.buttonSearch);
        buttonGps = findViewById(R.id.buttonGps);
        textViewCity = findViewById(R.id.textViewCity);
        textViewTemperature = findViewById(R.id.textViewTemperature);
        textViewDescription = findViewById(R.id.textViewDescription);
        imageViewWeatherIcon = findViewById(R.id.imageViewWeatherIcon); // Garder si vous utilisez les deux ou comme fallback
        customIconView = findViewById(R.id.customIcon);
        progressBarLoading = findViewById(R.id.progressBarLoading);
        recyclerViewForecast = findViewById(R.id.recyclerViewForecast);

        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setupRecyclerView();
        setupObservers();
        setupButtonClickListeners();
    }

    private void setupRecyclerView() {
        forecastAdapter = new ForecastAdapter(this);
        recyclerViewForecast.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewForecast.setAdapter(forecastAdapter);
    }

    private void setupObservers() {
        weatherViewModel.getWeatherData().observe(this, new Observer<WeatherData>() {
            @Override
            public void onChanged(WeatherData weatherData) {
                if (weatherData != null) {
                    updateUI(weatherData);
                }
            }
        });

        weatherViewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                progressBarLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        weatherViewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (error != null && !error.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Erreur: " + error, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void setupButtonClickListeners() {
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = editTextCity.getText().toString().trim();
                if (!city.isEmpty()) {
                    weatherViewModel.fetchWeatherByCity(city);
                } else {
                    Toast.makeText(MainActivity.this, "Veuillez entrer une ville", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLocationPermission();
            }
        });
    }

    private void updateUI(WeatherData data) {
        textViewCity.setText(data.getName());
        if (data.getMain() != null) {
            String temperatureString = String.format("%.1f°C", data.getMain().getTemp());
            textViewTemperature.setText(temperatureString);
        } else {
            textViewTemperature.setText("N/A");
        }

        if (data.getWeather() != null && !data.getWeather().isEmpty()) {
            WeatherData.WeatherInfo weatherInfo = data.getWeather().get(0);
            textViewDescription.setText(weatherInfo.getDescription());


            String condition = weatherInfo.getMain();
            customIconView.setWeatherCondition(condition);
            imageViewWeatherIcon.setVisibility(View.GONE);

        } else {
            textViewDescription.setText("N/A");
            customIconView.setWeatherCondition("unknown");
            imageViewWeatherIcon.setImageResource(R.mipmap.ic_launcher);
            imageViewWeatherIcon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_about) {
            Intent aboutIntent = new Intent(this, AboutActivity.class);
            startActivity(aboutIntent);
            return true;
        } else if (itemId == R.id.action_settings) {
            Toast.makeText(this, "Réglages (à implémenter)", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fetchWeatherForCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchWeatherForCurrentLocation();
            } else {
                Toast.makeText(this, "Permission de localisation refusée.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void fetchWeatherForCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission de localisation nécessaire", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBarLoading.setVisibility(View.VISIBLE);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        progressBarLoading.setVisibility(View.GONE);
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            weatherViewModel.fetchWeatherByCoordinates(latitude, longitude);
                        } else {
                            Toast.makeText(MainActivity.this, "Impossible d'obtenir la localisation actuelle.", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBarLoading.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Erreur localisation: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

}