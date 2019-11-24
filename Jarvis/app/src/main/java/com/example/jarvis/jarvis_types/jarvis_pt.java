package com.example.jarvis.jarvis_types;


//Preferred Period the user wants the event to happen
public class jarvis_pt {
    private String startDatenTime, endDatenTime;

    public jarvis_pt(String startDatenTime, String endDatenTime) {
        this.startDatenTime = startDatenTime;
        this.endDatenTime = endDatenTime;
    }

    public String getStartDatenTime() {
        return startDatenTime;
    }

    public String getEndDatenTime() {
        return endDatenTime;
    }
}
