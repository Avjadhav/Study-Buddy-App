package com.example.studybuddy;

import com.google.firebase.database.ServerValue;

import java.util.Map;

public class Notice {

    private String text;
    private String userId;
    private String name;
    private String year;
    private long time;


    public Notice(){
    }

    public Notice(String text, String userId, String name, String year) {
        this.text = text;
        this.userId = userId;
        this.name = name;
        this.year = year;
        this.time = System.currentTimeMillis()/1000;
    }

    public Notice(String text, String userId, String name) {
        this.text = text;
        this.userId = userId;
        this.name = name;
        this.time = System.currentTimeMillis()/1000;;
    }

    @Override
    public String toString() {
        return "Notice{" +
                "text='" + text + '\'' +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", year='" + year + '\'' +
                ", time=" + time +
                '}';
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


}
