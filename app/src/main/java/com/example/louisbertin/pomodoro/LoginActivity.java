package com.example.louisbertin.pomodoro;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.louisbertin.pomodoro.entity.User;
import com.example.louisbertin.pomodoro.repository.UserListener;
import com.example.louisbertin.pomodoro.repository.UserRepository;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button fbLoginButton;
    Button logoutButton;
    CallbackManager mCallbackManager = CallbackManager.Factory.create();

    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mSignUpButton, mLoginButton;

    private static final String TAG = "pwt";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_login);

        logoutButton = findViewById(R.id.logout_button);
        logoutButton.setVisibility(View.INVISIBLE);

        setFacebookLogin();
        setLogin();
    }

    @Override
    public void onStart() {
        super.onStart();

        // check is user is connected
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            hideLoginButton();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    // sign out user on click
    public void signOut(View view) {
        mAuth.signOut();
        showLoginButton();
        fbLoginButton.performClick();
    }

    private void setFacebookLogin() {
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });

        fbLoginButton = findViewById(R.id.login_button);
        fbLoginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email"));
            }

        });
    }

    private void setLogin() {
        mEmailField = findViewById(R.id.email);
        mPasswordField = findViewById(R.id.password);

        mLoginButton = findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
            }
        });

        mSignUpButton = findViewById(R.id.email_register_button);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            hideLoginButton();

                            // insert user if he doesn't exist
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            final User user = new User(currentUser.getUid(), currentUser.getDisplayName(), currentUser.getEmail());
                            final UserRepository userRepository = new UserRepository();
                            userRepository.getCurrentUser(new UserListener() {
                                @Override
                                public void onStart() {
                                }

                                @Override
                                public void onSuccess(DataSnapshot data) {
                                    if (data.getValue() == null) {
                                        userRepository.writeNewUser(user);
                                    }
                                }

                                @Override
                                public void onFailed(DatabaseError databaseError) {
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    // show login button
    private void hideLoginButton() {
        logoutButton.setVisibility(View.VISIBLE);
        fbLoginButton.setVisibility(View.INVISIBLE);
        mLoginButton.setVisibility(View.INVISIBLE);
        mSignUpButton.setVisibility(View.INVISIBLE);
    }

    // hide login button
    private void showLoginButton() {
        logoutButton.setVisibility(View.INVISIBLE);
        fbLoginButton.setVisibility(View.VISIBLE);
        mLoginButton.setVisibility(View.VISIBLE);
        mSignUpButton.setVisibility(View.VISIBLE);
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            hideLoginButton();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            UserRepository userRepository = new UserRepository();
                            userRepository.getCurrentUser(new UserListener() {
                                @Override
                                public void onStart() {

                                }

                                @Override
                                public void onSuccess(DataSnapshot data) {
                                    User user = data.getValue(User.class);
                                }

                                @Override
                                public void onFailed(DatabaseError databaseError) {

                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END sign_in_with_email]
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

}
