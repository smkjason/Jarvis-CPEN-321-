package com.example.jarvis.model;

public class Events {
    private String event_name;
    private String imageUrl;

    public Events(String event_name, String imageUrl) {
        this.event_name = event_name;
        this.imageUrl = imageUrl;
    }

    public Events(){

    }

    public String getEventname() {
        return event_name;
    }

    public void setEventname(String event_name) {
        this.event_name = event_name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
