package com.example.teophe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teophe.adapter.HistoryAdapter;
import com.example.teophe.database.DatabaseHelper;
import com.example.teophe.model.WeatherData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class HistoryActivity extends AppCompatActivity implements HistoryAdapter.OnHistoryItemClickListener {

    private Toolbar toolbar;
    private RecyclerView rvHistory;
    private TextView tvEmptyHistory;
    private FloatingActionButton fabClearHistory;

    private DatabaseHelper databaseHelper;
    private HistoryAdapter historyAdapter;
    private List<WeatherData> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        toolbar = findViewById(R.id.toolbar);
        rvHistory = findViewById(R.id.rvHistory);
        tvEmptyHistory = findViewById(R.id.tvEmptyHistory);
        fabClearHistory = findViewById(R.id.fabClearHistory);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        databaseHelper = new DatabaseHelper(this);

        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        loadHistoryData();

        fabClearHistory.setOnClickListener(v -> confirmClearHistory());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadHistoryData() {
        historyList = databaseHelper.getAllWeatherData();

        if (historyList.isEmpty()) {
            rvHistory.setVisibility(View.GONE);
            tvEmptyHistory.setVisibility(View.VISIBLE);
        } else {
            rvHistory.setVisibility(View.VISIBLE);
            tvEmptyHistory.setVisibility(View.GONE);

            if (historyAdapter == null) {
                historyAdapter = new HistoryAdapter(historyList, this);
                rvHistory.setAdapter(historyAdapter);
            } else {
                historyAdapter = new HistoryAdapter(historyList, this);
                rvHistory.setAdapter(historyAdapter);
            }
        }
    }

    @Override
    public void onHistoryItemClick(WeatherData weatherData) {
        Intent intent = new Intent(this, WeatherActivity.class);
        intent.putExtra("CITY_NAME", weatherData.getCityName());
        startActivity(intent);
    }

    @Override
    public void onHistoryItemLongClick(WeatherData weatherData, View view) {
        Log.d("HistoryActivity", "Long click sur: " + weatherData.getCityName());

        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.history_item_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_delete_item) {
                confirmDeleteItem(weatherData);
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void confirmDeleteItem(WeatherData weatherData) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_history_item_title)
                .setMessage(getString(R.string.delete_history_item_message, weatherData.getCityName()))
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    deleteHistoryItem(weatherData);
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void deleteHistoryItem(WeatherData weatherData) {
        boolean deleted = databaseHelper.deleteWeatherDataById(weatherData.getId());
        if (deleted) {
            Toast.makeText(this, getString(R.string.item_deleted_success, weatherData.getCityName()), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.item_deleted_error, Toast.LENGTH_SHORT).show();
        }
        loadHistoryData();
    }


    private void confirmClearHistory() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.clear_history)
                .setMessage(R.string.clear_history_confirmation)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    clearHistory();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void clearHistory() {
        databaseHelper.clearWeatherData();

        Snackbar.make(rvHistory, R.string.history_cleared, Snackbar.LENGTH_LONG).show();

        loadHistoryData();
    }
}