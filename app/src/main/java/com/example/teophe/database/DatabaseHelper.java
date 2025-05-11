package com.example.teophe.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.teophe.model.RecentSearch;
import com.example.teophe.model.WeatherData;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "weather_app.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_WEATHER_DATA = "weather_data";
    private static final String TABLE_RECENT_SEARCHES = "recent_searches";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CITY_NAME = "city_name";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    public static final String COLUMN_TEMPERATURE = "temperature";
    public static final String COLUMN_FEELS_LIKE = "feels_like";
    public static final String COLUMN_HUMIDITY = "humidity";
    public static final String COLUMN_PRESSURE = "pressure";
    public static final String COLUMN_WIND_SPEED = "wind_speed";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_WEATHER_CODE = "weather_code";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_SUNRISE = "sunrise";
    public static final String COLUMN_SUNSET = "sunset";
    public static final String COLUMN_ICON_ID_API = "icon_id_api";


    private static final String CREATE_TABLE_WEATHER_DATA = "CREATE TABLE " + TABLE_WEATHER_DATA + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CITY_NAME + " TEXT,"
            + COLUMN_TEMPERATURE + " REAL,"
            + COLUMN_FEELS_LIKE + " REAL,"
            + COLUMN_HUMIDITY + " INTEGER,"
            + COLUMN_PRESSURE + " INTEGER,"
            + COLUMN_WIND_SPEED + " REAL,"
            + COLUMN_DESCRIPTION + " TEXT,"
            + COLUMN_WEATHER_CODE + " INTEGER,"
            + COLUMN_ICON_ID_API + " TEXT,"
            + COLUMN_LATITUDE + " REAL,"
            + COLUMN_LONGITUDE + " REAL,"
            + COLUMN_SUNRISE + " INTEGER,"
            + COLUMN_SUNSET + " INTEGER,"
            + COLUMN_TIMESTAMP + " INTEGER"
            + ")";

    private static final String CREATE_TABLE_RECENT_SEARCHES = "CREATE TABLE " + TABLE_RECENT_SEARCHES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CITY_NAME + " TEXT UNIQUE,"
            + COLUMN_TIMESTAMP + " INTEGER"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_WEATHER_DATA);
        db.execSQL(CREATE_TABLE_RECENT_SEARCHES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEATHER_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECENT_SEARCHES);

        onCreate(db);
    }

    public long insertWeatherData(WeatherData weatherData) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = -1;

        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(COLUMN_CITY_NAME, weatherData.getCityName());
            values.put(COLUMN_TEMPERATURE, weatherData.getTemperature());
            values.put(COLUMN_FEELS_LIKE, weatherData.getFeelsLike());
            values.put(COLUMN_HUMIDITY, weatherData.getHumidity());
            values.put(COLUMN_PRESSURE, weatherData.getPressure());
            values.put(COLUMN_WIND_SPEED, weatherData.getWindSpeed());
            values.put(COLUMN_DESCRIPTION, weatherData.getDescription());
            values.put(COLUMN_WEATHER_CODE, weatherData.getWeatherCode());
            values.put(COLUMN_ICON_ID_API, weatherData.getIconIdApi());
            values.put(COLUMN_LATITUDE, weatherData.getLatitude());
            values.put(COLUMN_LONGITUDE, weatherData.getLongitude());
            values.put(COLUMN_SUNRISE, weatherData.getSunrise());
            values.put(COLUMN_SUNSET, weatherData.getSunset());
            values.put(COLUMN_TIMESTAMP, weatherData.getTimestamp());

            id = db.insert(TABLE_WEATHER_DATA, null, values);

            if (id != -1 && weatherData.getCityName() != null && !weatherData.getCityName().isEmpty()) {
                ContentValues recentSearchValues = new ContentValues();
                recentSearchValues.put(COLUMN_CITY_NAME, weatherData.getCityName());
                recentSearchValues.put(COLUMN_TIMESTAMP, System.currentTimeMillis());
                db.insertWithOnConflict(TABLE_RECENT_SEARCHES, null, recentSearchValues, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error inserting weather data", e);
        } finally {
            if (db != null && db.inTransaction()) {
                db.endTransaction();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return id;
    }

    public List<WeatherData> getAllWeatherData() {
        List<WeatherData> weatherDataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        String selectQuery = "SELECT * FROM " + TABLE_WEATHER_DATA + " ORDER BY " + COLUMN_TIMESTAMP + " DESC";

        try {
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    WeatherData weatherData = new WeatherData(
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CITY_NAME)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TEMPERATURE)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_FEELS_LIKE)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HUMIDITY)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRESSURE)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_WIND_SPEED)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WEATHER_CODE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ICON_ID_API)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE)),
                            cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SUNRISE)),
                            cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SUNSET)),
                            cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
                    );
                    weatherData.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                    weatherDataList.add(weatherData);
                } while (cursor.moveToNext());
            }
        } catch (IllegalArgumentException e) {
            Log.e("DatabaseHelper", "Column not found in getAllWeatherData: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return weatherDataList;
    }

    public void clearWeatherData() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_WEATHER_DATA, null, null);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error clearing weather data", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public boolean deleteWeatherDataById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = 0;
        try {
            rowsAffected = db.delete(TABLE_WEATHER_DATA, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error deleting weather data by ID: " + id, e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return rowsAffected > 0;
    }


    public long insertRecentSearch(RecentSearch recentSearch) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CITY_NAME, recentSearch.getCityName());
            values.put(COLUMN_TIMESTAMP, recentSearch.getTimestamp());

            id = db.insertWithOnConflict(TABLE_RECENT_SEARCHES, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error inserting recent search", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return id;
    }

    public List<RecentSearch> getAllRecentSearches(int limit) {
        List<RecentSearch> recentSearchesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        String query = "SELECT * FROM " + TABLE_RECENT_SEARCHES
                + " ORDER BY " + COLUMN_TIMESTAMP + " DESC"
                + " LIMIT " + limit;
        try {
            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    RecentSearch search = new RecentSearch(
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CITY_NAME)),
                            cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
                    );
                    search.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                    recentSearchesList.add(search);
                } while (cursor.moveToNext());
            }
        } catch (IllegalArgumentException e) {
            Log.e("DatabaseHelper", "Column not found in getAllRecentSearches: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return recentSearchesList;
    }

    public void clearRecentSearches() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_RECENT_SEARCHES, null, null);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error clearing recent searches", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }
}