package com.example.jarvis.jarvis_types;

import java.util.ArrayList;
import java.util.Date;

public class jarvisevent {
    private String name_of_event;
    private Date due;
    private String eventid;
    private String admin;
    private String length;
    private ArrayList<String> attendees;
    private ArrayList<String> invitees;

    //For Pending Events
    public jarvisevent(String name_of_event, String eventid, String admin, String length) {
        this.name_of_event = name_of_event;
        this.eventid = eventid;
        this.admin = admin;
        this.length = length;
    }

    //for list of events that are happening (Fragment)
    public jarvisevent(String name_of_event, String eventid) {
        this.name_of_event = name_of_event;
        this.eventid = eventid;
    }

    public String getName_of_event() {
        return name_of_event;
    }

    public Date getDue() {
        return due;
    }

    public String getEventid() {
        return eventid;
    }

    public String getAdmin() {
        return admin;
    }

    public String getLength() {
        return length;
    }

    public ArrayList<String> getAttendees() {
        return attendees;
    }

    public ArrayList<String> getInvitees() {
        return invitees;
    }
}
