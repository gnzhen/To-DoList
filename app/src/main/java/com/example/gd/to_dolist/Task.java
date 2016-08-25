package com.example.gd.to_dolist;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by GD on 7/21/2016.
 */
public class Task implements java.io.Serializable {

    private static final long serialVersionUID = 4209360273818925922L;
    public static SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
    //property
    private long id;
    private String desc;
    private String date;
    private String time;
    private String status;

    public Task(){}
    //constructor
    public Task(long id, String desc, String date, String time, String status){
        this.id = id;
        this.desc = desc;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public long getId() { return id; }
    public String getDesc() { return desc; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getStatus() { return status; }

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
    public void setStatus(String status) {
        this.status = status;
    }


    public boolean checkOverdue() {
        Calendar c = Calendar.getInstance();
        Long time_now = c.getTimeInMillis();

        return (Double.parseDouble(time) < Double.parseDouble(Long.toString(time_now))
                && Double.parseDouble(date) <= Double.parseDouble(Long.toString(time_now)));
    }

    public String convertDate(){
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormatter.format(Long.parseLong(date));
    }

    public String convertTime()
    {
        SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
        return timeFormatter.format(Long.parseLong(time));
    }
}
