package com.example.jarvis.jarvis_types;

public class jarvismessage {
    private String message;
    private String sender;
    private String time;
    private Boolean is_mine = false;


    public jarvismessage(String message, String sender, String time, Boolean is_mine) {
        this.message = message;
        this.sender = sender;
        this.time = time;
        this.is_mine = is_mine;
    }

    public jarvismessage(String message, String sender, String time) {
        this.message = message;
        this.sender = sender;
        this.time = time;
        is_mine = false;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getTime() {
        return time;
    }

    public Boolean get_is_mine(){ return is_mine; }
}
