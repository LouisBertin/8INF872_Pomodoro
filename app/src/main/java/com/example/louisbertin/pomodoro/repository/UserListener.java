package com.example.louisbertin.pomodoro.repository;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public interface UserListener {
    public void onStart();
    public void onSuccess(DataSnapshot data);
    public void onFailed(DatabaseError databaseError);
}