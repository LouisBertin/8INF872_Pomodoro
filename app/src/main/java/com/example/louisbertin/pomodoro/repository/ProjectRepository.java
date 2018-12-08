package com.example.louisbertin.pomodoro.repository;

import com.example.louisbertin.pomodoro.entity.Project;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProjectRepository {
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private UserRepository userRepository = new UserRepository();

    public ProjectRepository() {
        // db instance
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            currentUser = user;
        }
    }

    public void writeNewProject(Project project) {
        String key = mDatabase.push().getKey();

        mDatabase.child("projects").child(key).setValue(project);
        userRepository.addProject(key);
    }

}
