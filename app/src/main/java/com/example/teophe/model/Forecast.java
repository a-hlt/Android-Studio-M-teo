package com.example.teophe.model;

public class Forecast {
    private String dayName;
    private String date;
    private String iconIdApi;
    private String description;
    private double tempMax;
    private double tempMin;

    public Forecast(String dayName, String date, String iconIdApi, String description, double tempMax, double tempMin) {
        this.dayName = dayName;
        this.date = date;
        this.iconIdApi = iconIdApi;
        this.description = description;
        this.tempMax = tempMax;
        this.tempMin = tempMin;
    }

    public String getDayName() { return dayName; }
    public String getDate() { return date; }
    public String getIconIdApi() { return iconIdApi; }
    public String getDescription() { return description; }
    public double getTempMax() { return tempMax; }
    public double getTempMin() { return tempMin; }
}