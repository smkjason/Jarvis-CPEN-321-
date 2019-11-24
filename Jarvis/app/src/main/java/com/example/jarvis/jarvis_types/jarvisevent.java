package com.example.jarvis.jarvis_types;

import java.util.ArrayList;
import java.util.Date;

public class jarvisevent {
    private String name_of_event;
    private Date due;
    private String eventid;
    private String tentative_event_id;
    private String admin;
    private String length;
    private ArrayList<String> attendees;
    private ArrayList<String> invitees;
    private Boolean Actual;

    //For Pending Events
    public jarvisevent(String name_of_event, String eventid, String admin, String length) {
        this.name_of_event = name_of_event;
        this.tentative_event_id = eventid;
        this.admin = admin;
        this.length = length;
        Actual = false;
    }

    public jarvisevent(String name_of_event, Date due, String eventid, String admin,
                       String length, ArrayList<String> attendees,
                       ArrayList<String> invitees, Boolean real) {
        this.name_of_event = name_of_event;
        this.due = due;
        this.eventid = eventid;
        this.admin = admin;
        this.length = length;
        this.attendees = attendees;
        this.invitees = invitees;
        this.tentative_event_id = null;
        Actual = real;
    }

    public jarvisevent(String name_of_event, String eventid, String admin) {
        this.name_of_event = name_of_event;
        this.eventid = eventid;
        this.admin = admin;
        Actual = true;
    }

    public String getName_of_event() {
        return name_of_event;
    }

    public Date getDue() {
        return due;
    }

    public String getTentative_event_id() {
        return tentative_event_id;
    }

    public String getAdmin() {
        return admin;
    }

    public String getEventid() {
        return eventid;
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
