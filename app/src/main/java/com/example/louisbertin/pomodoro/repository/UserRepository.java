package com.example.louisbertin.pomodoro.repository;

import android.util.Log;

import com.example.louisbertin.pomodoro.entity.Project;
import com.example.louisbertin.pomodoro.entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserRepository {
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    // getProjects vars
    private int cpt = 0;
    private ArrayList<Project> projectList = new ArrayList<>();

    public UserRepository() {
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            currentUser = user;
        }
    }

    public void writeNewUser(User user) {
        mDatabase.child("users").child(user.getUuid()).setValue(user);
    }

    public void addProject(String projectId) {
        mDatabase.child("user_projects").child(currentUser.getUid()).child(projectId).setValue(true);
    }

    public void getCurrentUser(final UserListener listener) {
        listener.onStart();
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("tag", "loadPost:onCancelled", databaseError.toException());
            }
        };

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference mUserReference = mDatabase.child("users").child(user.getUid());
        mUserReference.addValueEventListener(postListener);
    }

    public void getProjects(final DataListener dataListener) {
        // fetch users projects ids
        DatabaseReference user_projects = FirebaseDatabase.getInstance().getReference().child("user_projects").child(currentUser.getUid());
        user_projects.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final long valueNumber = dataSnapshot.getChildrenCount();

                if (dataSnapshot.exists())
                {
                    // fetch project by id
                    for (DataSnapshot projects : dataSnapshot.getChildren()) {
                        DatabaseReference projectsDb = FirebaseDatabase.getInstance().getReference().child("projects").child(projects.getKey());
                        projectsDb.addListenerForSingleValueEvent(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Project post = dataSnapshot.getValue(Project.class);
                                post.setUuid(dataSnapshot.getKey());
                                projectList.add(post);
                                cpt++;

                                // return data
                                if (cpt == valueNumber) {
                                    dataListener.newDataReceived(projectList);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) { }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

}
