package com.example.jarvis.jarvis_types;

public class TentativeEventItem {
    private String mTitle;
    private String mDeadline;
    private String mEventId;

    public TentativeEventItem(String title, String deadline, String eventId){
        mTitle = title;
        mDeadline = deadline;
        mEventId = eventId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDeadline(){
        return mDeadline;
    }

    public String getEventId() {return mEventId;}
}
