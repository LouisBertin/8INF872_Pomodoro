package com.example.louisbertin.pomodoro;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
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
    Button loginButton;
    Button loginFacebookButton;
    Button signUpButton;

    @Deprecated
    Button logoutButton;

    private AppCompatAutoCompleteTextView userMail;
    private EditText userPassword;

    CallbackManager mCallbackManager = CallbackManager.Factory.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        /*
        logoutButton = findViewById(R.id.logout_button);
        logoutButton.setVisibility(View.INVISIBLE);
        */

        setLogin();
        setFacebookLogin();
        setSignup();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /*
    * Bind le bouton de retour à l'activité principale
    * Autrement pourrait retourner X fois sur SignupActivity
    * */
    @Override
    public void onBackPressed() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    private void setLogin() {
        userMail = findViewById(R.id.signup_mail);
        userPassword = findViewById(R.id.login_password);

        loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("login", "Login not yet implemented here");
                Log.d("login", "your password is " + userPassword.getText() + " lmao");
            }
        });
    }

    private void setFacebookLogin() {
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    private static final String TAG = "pwt";

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

        loginFacebookButton = findViewById(R.id.login_button_facebook);
        loginFacebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
            }
        });
    }

    public void setSignup() {
        signUpButton = findViewById(R.id.login_signup);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
            }
        });
    }

    // sign out user on click
    public void signOut(View view) {
        mAuth.signOut();
        showLoginButton();
        loginButton.performClick();
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("pwt", "signInWithCredential:success");
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
                            Log.w("pwt", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI();
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
        loginButton.setVisibility(View.INVISIBLE);
    }

    // hide login button
    private void showLoginButton() {
        logoutButton.setVisibility(View.INVISIBLE);
        loginButton.setVisibility(View.VISIBLE);
    }
}
