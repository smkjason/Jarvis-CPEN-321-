package com.example.jarvis.jarvis_types;

import android.widget.CalendarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class jarvismessage {
    private String message;
    private String sender;
    private String time;
    private Boolean is_mine = false;


    public jarvismessage(String sender, String message){
        this.sender = sender;
        this.message = message;
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh: mm");
        this.time = timeFormat.format(Calendar.getInstance().getTime());
        is_mine = true;
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
