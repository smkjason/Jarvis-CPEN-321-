package com.example.jarvis.jarvis_types;

public class SelectTimeItem {
    private String mStart;
    private String mEnd;
    private int rank;

    public SelectTimeItem(String start, String end){
        mStart = start;
        mEnd = end;
    }

    public SelectTimeItem(String start, int rank){
        mStart = start;
        this.rank = rank;
    }

    public String getStart() {
        return mStart;
    }

    public String getEnd(){
        return mEnd;
    }

    public int getRank() {
        return rank;
    }
}
