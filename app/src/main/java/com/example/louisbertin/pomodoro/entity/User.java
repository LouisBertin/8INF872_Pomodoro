package com.example.louisbertin.pomodoro.entity;

public class User {
    public String uuid;
    public String username;
    public String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String uuid, String username, String email) {
        this.uuid = uuid;
        this.username = username;
        this.email = email;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
