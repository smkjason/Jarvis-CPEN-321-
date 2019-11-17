package com.example.jarvis;

public class jarvisevent {
    private String name_of_event;
    private String eventid;

    public jarvisevent(String name_of_event, String eventid) {
        this.name_of_event = name_of_event;
        this.eventid = eventid;
    }

    public String getName_of_event() {
        return name_of_event;
    }
    public String getEventid() {
        return eventid;
    }

}
