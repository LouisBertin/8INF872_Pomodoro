package com.example.louisbertin.pomodoro.repository;

import android.support.annotation.NonNull;

import com.example.louisbertin.pomodoro.entity.Project;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    public void deleteProjectById(String id) {
        mDatabase.child("projects").child(id).removeValue();
        mDatabase.child("user_projects").child(currentUser.getUid()).child(id).removeValue();
    }

    public void getProjectById(String id) {
        final DatabaseReference project = mDatabase.child("projects").child(id);
        project.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // TODO : fetch project todos
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

}
