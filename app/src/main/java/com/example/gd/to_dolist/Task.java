package com.example.gd.to_dolist;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by GD on 7/21/2016.
 */
public class Task implements java.io.Serializable {

    //property
    private String desc;
    private String date;
    private String time;

    //constructor
    public Task(String desc, String date, String time){
        this.desc = desc;
        this.date = date;
        this.time = time;
    }

    public String getDesc() { return desc; }
    public String getDate() { return date; }
    public String getTime() { return time; }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setTime(String time) {
        this.time = time;
    }
}
