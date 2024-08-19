package com.android.example.alertme;

public class Task {
    private String title;
    private int hour;
    private int minute;
    private String description;
    private String mediaUri;

    public Task(String title, int hour, int minute, String description) {
        this.title = title;
        this.hour = hour;
        this.minute = minute;
        this.description = description;
    }

    // Add getters and setters as needed
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMediaUri(String mediaUri) {
        this.mediaUri = mediaUri;
    }

    public String getMediaUri() {
        return mediaUri;
    }
}
