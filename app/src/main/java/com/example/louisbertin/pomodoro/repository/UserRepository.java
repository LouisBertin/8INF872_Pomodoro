package com.example.louisbertin.pomodoro.repository;

import android.util.Log;

import com.example.louisbertin.pomodoro.entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserRepository {
    private DatabaseReference mDatabase;

    public UserRepository() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void writeNewUser(User user) {
        mDatabase.child("users").child(user.getUuid()).setValue(user);
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
}
