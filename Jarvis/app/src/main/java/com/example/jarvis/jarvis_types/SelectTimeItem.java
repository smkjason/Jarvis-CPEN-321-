package com.example.jarvis.jarvis_types;

public class SelectTimeItem {
    private String mDate;
    private String mTime;
    private String mSelected;

    public SelectTimeItem(String date, String time, String selected){
        mDate = date;
        mTime = time;
        mSelected = selected;
    }

    public String getDate() {
        return mDate;
    }

    public String getTime(){
        return mTime;
    }

    public String getSelected() {return mSelected;}

    public void selected() {
        mSelected = "  Selected";
    }

}
