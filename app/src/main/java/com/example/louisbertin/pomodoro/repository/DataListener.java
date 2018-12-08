package com.example.louisbertin.pomodoro.repository;

import com.example.louisbertin.pomodoro.entity.Project;

import java.util.ArrayList;

public interface DataListener {
    void newDataReceived(ArrayList<Project> recipeList);
}
