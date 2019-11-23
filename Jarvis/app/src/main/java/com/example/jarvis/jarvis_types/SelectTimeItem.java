package com.example.jarvis.jarvis_types;

public class SelectTimeItem {
    private String mStart;
    private String mEnd;

    public SelectTimeItem(String start, String end){
        mStart = start;
        mEnd = end;
    }

    public String getStart() {
        return mStart;
    }

    public String getEnd(){
        return mEnd;
    }
}
