package com.example.jarvis.jarvis_types;


import java.util.Date;

//Preferred Period the user wants the event to happen
public class jarvis_pt {
    private Date starttime, endtime;

    public jarvis_pt(Date starttime, Date endtime) {
        this.starttime = starttime;
        this.endtime = endtime;
    }

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }
}
