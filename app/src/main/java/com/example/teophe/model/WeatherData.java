package com.example.teophe.model;

public class WeatherData {
    private int id;
    private String cityName;
    private double temperature;
    private double feelsLike;
    private int humidity;
    private int pressure;
    private double windSpeed;
    private String description;
    private int weatherCode;
    private String iconIdApi;
    private double latitude;
    private double longitude;
    private long sunrise;
    private long sunset;
    private long timestamp;

    public WeatherData(String cityName, double temperature, double feelsLike, int humidity, int pressure,
                       double windSpeed, String description, int weatherCode, String iconIdApi, double latitude,
                       double longitude, long sunrise, long sunset, long timestamp) {
        this.cityName = cityName;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.humidity = humidity;
        this.pressure = pressure;
        this.windSpeed = windSpeed;
        this.description = description;
        this.weatherCode = weatherCode;
        this.iconIdApi = iconIdApi;
        this.latitude = latitude;
        this.longitude = longitude;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public String getCityName() { return cityName; }
    public double getTemperature() { return temperature; }
    public double getFeelsLike() { return feelsLike; }
    public int getHumidity() { return humidity; }
    public int getPressure() { return pressure; }
    public double getWindSpeed() { return windSpeed; }
    public String getDescription() { return description; }
    public int getWeatherCode() { return weatherCode; }
    public String getIconIdApi() { return iconIdApi; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public long getSunrise() { return sunrise; }
    public long getSunset() { return sunset; }
    public long getTimestamp() { return timestamp; }

    public void setId(int id) { this.id = id; }
    public void setCityName(String cityName) { this.cityName = cityName; }
}