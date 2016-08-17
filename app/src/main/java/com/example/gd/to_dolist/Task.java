package com.example.gd.to_dolist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by GD on 7/21/2016.
 */
public class Task implements java.io.Serializable {

    //property
    private long id;
    private String desc;
    private String date;
    private String time;
    private boolean isDone;

    public Task(){};

    //constructor
    public Task(long id, String desc, String date, String time, boolean isDone){
        this.id = id;
        this.desc = desc;
        this.date = date;
        this.time = time;
        this.isDone = isDone;
    }

    public long getId() { return id; }
    public String getDesc() { return desc; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public Boolean isDone() { return isDone; }

    public void setId(long id) {
        this.id = id;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public void setIsDone(boolean isDone) {
        this.isDone = isDone;
    }

    public boolean checkOverdue() {
        Calendar c = Calendar.getInstance();
        int time_now = Integer.parseInt(Long.toString(c.getTimeInMillis()));
        if(Integer.parseInt(date) >= time_now && Integer.parseInt(time) >= time_now)
            return true;
        else
            return false;
    }
}
