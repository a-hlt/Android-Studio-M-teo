package com.example.meteoapp.data;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherData {
    @SerializedName("coord")
    private CoordData coord;

    @SerializedName("weather")
    private List<WeatherInfo> weather;

    @SerializedName("main")
    private MainData main;

    @SerializedName("wind")
    private WindData wind;

    @SerializedName("name")
    private String name;

    public CoordData getCoord() { return coord; }
    public List<WeatherInfo> getWeather() { return weather; }
    public MainData getMain() { return main; }
    public WindData getWind() { return wind; }
    public String getName() { return name; }

    public static class CoordData {
        @SerializedName("lon") private double lon;
        @SerializedName("lat") private double lat;
        public double getLon() { return lon; }
        public double getLat() { return lat; }
    }

    public static class WeatherInfo {
        @SerializedName("id") private int id;
        @SerializedName("main") private String main;
        @SerializedName("description") private String description;
        @SerializedName("icon") private String icon;
        public int getId() { return id; }
        public String getMain() { return main; }
        public String getDescription() { return description; }
        public String getIcon() { return icon; }
    }

    public static class MainData {
        @SerializedName("temp") private double temp;
        @SerializedName("feels_like") private double feelsLike;
        @SerializedName("temp_min") private double tempMin;
        @SerializedName("temp_max") private double tempMax;
        @SerializedName("pressure") private int pressure;
        @SerializedName("humidity") private int humidity;
        public double getTemp() { return temp; }
        public double getFeelsLike() { return feelsLike; }
        public double getTempMin() { return tempMin; }
        public double getTempMax() { return tempMax; }
        public int getPressure() { return pressure; }
        public int getHumidity() { return humidity; }
    }

    public static class WindData {
        @SerializedName("speed") private double speed;
        @SerializedName("deg") private int deg;
        public double getSpeed() { return speed; }
        public int getDeg() { return deg; }
    }
}