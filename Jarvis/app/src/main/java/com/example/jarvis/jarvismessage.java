package com.example.jarvis;

import android.widget.CalendarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class jarvismessage {
    public String message;
    public String sender;
    public String time;

    public jarvismessage(String sender, String message){
        this.sender = sender;
        this.message = message;
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh: mm");
        this.time = timeFormat.format(Calendar.getInstance().getTime());
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTime() {
        return time;
    }
}
