package com.example.teophe;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teophe.adapter.RecentSearchRecyclerAdapter;
import com.example.teophe.database.DatabaseHelper;
import com.example.teophe.model.RecentSearch;
import com.example.teophe.model.WeatherPreferences;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private TextInputEditText etCitySearch;
    private Button btnSearch;
    private Button btnUseLocation;
    private Button btnHistory;

    private RecyclerView rvRecentSearches;
    private TextView tvEmptyRecentSearches;

    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private WeatherPreferences weatherPreferences;

    private RecentSearchRecyclerAdapter recentSearchesAdapter;
    private List<String> recentSearchesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCitySearch = findViewById(R.id.etCitySearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnUseLocation = findViewById(R.id.btnUseLocation);
        btnHistory = findViewById(R.id.btnHistory);
        rvRecentSearches = findViewById(R.id.rvRecentSearches);
        tvEmptyRecentSearches = findViewById(R.id.tvEmptyRecentSearches);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        databaseHelper = new DatabaseHelper(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        weatherPreferences = new WeatherPreferences(sharedPreferences);

        setupRecentSearchesRecyclerView();

        loadRecentSearchesData();

        setupClickListeners();

        setupSearchEditText();

    }

    private void setupRecentSearchesRecyclerView() {
        recentSearchesList = new ArrayList<>();
        rvRecentSearches.setLayoutManager(new LinearLayoutManager(this));
        recentSearchesAdapter = new RecentSearchRecyclerAdapter(recentSearchesList, cityName -> {
            launchWeatherActivity(cityName);
        });
        rvRecentSearches.setAdapter(recentSearchesAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadRecentSearchesData();

        String lastCity = weatherPreferences.getLastSearchedCity();
        if (etCitySearch != null && !lastCity.isEmpty() && etCitySearch.getText().toString().isEmpty()) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, R.string.location_permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.exit_app)
                .setMessage(R.string.exit_confirmation)
                .setPositiveButton(R.string.yes, (dialog, which) -> finishAffinity())
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void setupClickListeners() {
        btnSearch.setOnClickListener(v -> searchWeather());
        btnUseLocation.setOnClickListener(v -> {
            if (checkLocationPermission()) {
                getCurrentLocation();
            } else {
                requestLocationPermission();
            }
        });
        btnHistory.setOnClickListener(v -> {
            Intent historyIntent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(historyIntent);
        });
    }

    private void setupSearchEditText() {
        etCitySearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                searchWeather();
                return true;
            }
            return false;
        });
    }


    private void searchWeather() {
        String cityName = etCitySearch.getText().toString().trim();
        if (!cityName.isEmpty()) {
            saveSearchToLog(cityName);
            launchWeatherActivity(cityName);
            etCitySearch.setText("");
        } else {
            Toast.makeText(this, R.string.enter_city_name_prompt, Toast.LENGTH_SHORT).show();
        }
    }

    private void launchWeatherActivity(String cityName) {
        addToRecentSearches(cityName);
        weatherPreferences.setLastSearchedCity(cityName);
        Intent weatherIntent = new Intent(MainActivity.this, WeatherActivity.class);
        weatherIntent.putExtra("CITY_NAME", cityName);
        startActivity(weatherIntent);
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void getCurrentLocation() {
        if (checkLocationPermission()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission();
                return;
            }
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            launchWeatherActivityWithCoordinates(location.getLatitude(), location.getLongitude());
                        } else {
                            Toast.makeText(MainActivity.this, R.string.location_not_available, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void launchWeatherActivityWithCoordinates(double latitude, double longitude) {
        Intent weatherIntent = new Intent(MainActivity.this, WeatherActivity.class);
        weatherIntent.putExtra("LATITUDE", latitude);
        weatherIntent.putExtra("LONGITUDE", longitude);
        startActivity(weatherIntent);
    }

    private void loadRecentSearchesData() {
        List<RecentSearch> searchesFromDb = databaseHelper.getAllRecentSearches(5);

        recentSearchesList.clear();
        for (RecentSearch search : searchesFromDb) {
            recentSearchesList.add(search.getCityName());
        }

        if (recentSearchesList.isEmpty()) {
            rvRecentSearches.setVisibility(View.GONE);
            tvEmptyRecentSearches.setVisibility(View.VISIBLE);
        } else {
            rvRecentSearches.setVisibility(View.VISIBLE);
            tvEmptyRecentSearches.setVisibility(View.GONE);
        }
        recentSearchesAdapter.notifyDataSetChanged();
    }


    private void addToRecentSearches(String cityName) {
        long timestamp = System.currentTimeMillis();
        RecentSearch search = new RecentSearch(cityName, timestamp);
        databaseHelper.insertRecentSearch(search);
        loadRecentSearchesData();
    }

    private void saveSearchToLog(String cityName) {
        try {
            File logFile = new File(getFilesDir(), "search_log.txt");
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
            writer.append(timestamp).append(" - ").append(cityName).append("\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}