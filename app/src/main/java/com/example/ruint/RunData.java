package com.example.ruint;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RunData implements Serializable {
    private String id;
    private double distance; // em km
    private long duration; // em segundos
    private double averagePace; // em min/km
    private double calories;
    private long timestamp;
    private String dateString;

    public RunData() {
        // Construtor vazio necessário para Firebase
    }

    public RunData(double distance, long duration, double averagePace, double calories) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.distance = distance;
        this.duration = duration;
        this.averagePace = averagePace;
        this.calories = calories;
        this.timestamp = System.currentTimeMillis();
        this.dateString = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date(timestamp));
    }


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }

    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }

    public double getAveragePace() { return averagePace; }
    public void setAveragePace(double averagePace) { this.averagePace = averagePace; }

    public double getCalories() { return calories; }
    public void setCalories(double calories) { this.calories = calories; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getDateString() { return dateString; }
    public void setDateString(String dateString) { this.dateString = dateString; }

    public String getFormattedDistance() {
        return String.format(Locale.getDefault(), "%.2f km", distance);
    }

    public String getFormattedDuration() {
        long hours = duration / 3600;
        long minutes = (duration % 3600) / 60;
        long seconds = duration % 60;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }

    public String getFormattedPace() {
        int paceMinutes = (int) averagePace;
        int paceSeconds = (int) ((averagePace - paceMinutes) * 60);
        return String.format(Locale.getDefault(), "%02d:%02d /km", paceMinutes, paceSeconds);
    }

    public String getFormattedCalories() {
        return String.format(Locale.getDefault(), "%.0f cal", calories);
    }
}