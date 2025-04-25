package com.example.meteoapp.data;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ForecastItem {

    @SerializedName("dt")
    private long timestamp;

    @SerializedName("main")
    private WeatherData.MainData main;

    @SerializedName("weather")
    private List<WeatherData.WeatherInfo> weather;

    @SerializedName("clouds")
    private CloudsData clouds;

    @SerializedName("wind")
    private WeatherData.WindData wind;

    @SerializedName("visibility")
    private int visibility;

    @SerializedName("pop")
    private double probabilityOfPrecipitation;

    // @SerializedName("sys")
    // private SysData sys;

    @SerializedName("dt_txt")
    private String dateTimeString;


    public long getTimestamp() {
        return timestamp;
    }

    public WeatherData.MainData getMain() {
        return main;
    }

    public List<WeatherData.WeatherInfo> getWeather() {
        return weather;
    }

    public CloudsData getClouds() {
        return clouds;
    }

    public WeatherData.WindData getWind() {
        return wind;
    }

    public int getVisibility() {
        return visibility;
    }

    public double getProbabilityOfPrecipitation() {
        return probabilityOfPrecipitation;
    }

    /*
    public SysData getSys() {
        return sys;
    }
    */

    public String getDateTimeString() {
        return dateTimeString;
    }


    public static class CloudsData {
        @SerializedName("all")
        private int all;

        public int getAll() {
            return all;
        }
    }

    /*
    public static class SysData {
        @SerializedName("pod")
        private String partOfDay;

        public String getPartOfDay() {
            return partOfDay;
        }
    }
    */

}