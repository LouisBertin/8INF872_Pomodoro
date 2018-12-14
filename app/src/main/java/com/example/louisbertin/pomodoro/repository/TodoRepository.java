package com.example.louisbertin.pomodoro.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class TodoRepository {
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private UserRepository userRepository = new UserRepository();

    public TodoRepository() {
        // db instance
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            currentUser = user;
        }
    }

    public void writeTodos(String projectId, ArrayList<String> todosName) {
        System.out.println(projectId);
        System.out.println(todosName);

        if (!todosName.isEmpty()) {
            for(String todoName : todosName) {
                String key = mDatabase.child("projects").child(projectId).push().getKey();
                HashMap object = new HashMap();
                object.put("title", todoName);
                object.put("isComplete", false);

                mDatabase.child("projects").child(projectId).child("todos").child(key).setValue(object);
            }
        }
    }
}
