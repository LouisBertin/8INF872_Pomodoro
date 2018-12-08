package com.example.louisbertin.pomodoro.entity;

public class Project {
    public String uuid;
    public String title;

    public Project() {
        // empty constructor for Firebase
    }

    public Project(String title) {
        this.title = title;
    }

    public String getUuid() {
        return uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
