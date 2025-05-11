package com.example.teophe.model;

public class RecentSearch {
    private int id;
    private String cityName;
    private long timestamp;

    public RecentSearch(String cityName, long timestamp) {
        this.cityName = cityName;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public String getCityName() { return cityName; }
    public long getTimestamp() { return timestamp; }

    public void setId(int id) { this.id = id; }
}