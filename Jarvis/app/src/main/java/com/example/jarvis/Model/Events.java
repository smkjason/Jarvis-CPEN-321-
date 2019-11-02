package com.example.jarvis.Model;

import android.util.EventLog;

public class Events {
    private String event_name;
    private String imageUrl;

    public Events(String event_name, String imageUrl) {
        this.event_name = event_name;
        this.imageUrl = imageUrl;
    }

    public Events(){

    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
