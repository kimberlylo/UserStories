package com.example.lkimberly.userstories.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lkimberly.userstories.R;
import com.example.lkimberly.userstories.models.Job;
import com.example.lkimberly.userstories.models.Matches;
import com.example.lkimberly.userstories.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static java.security.AccessController.getContext;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main Activity";

    Button signInButton;
    Button signupButton;
    EditText username_et;
    EditText password_et;
    EditText email_et;

    ImageView iv_username_check;
    ImageView iv_password_check;

    List<Job> jobs = new ArrayList<>();

    ParseUser poster = new User();
    ParseUser subscriber = new User();

    Matches match;

    Matches m2 = new Matches();

    Job job = new Job();

    boolean one;
    boolean two;
    boolean three;

    Button testingFirebaseBtn;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // do stuff with the user
            Log.d("LoginActivity", "Login successful");
            final Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

        signInButton = findViewById(R.id.sign_in_btn);
        signupButton = findViewById(R.id.sign_up_button);
        username_et = findViewById(R.id.username_et);
        password_et = findViewById(R.id.password_et);
        email_et = findViewById(R.id.email_et);

        iv_username_check = findViewById(R.id.iv_username_check);
        iv_password_check = findViewById(R.id.iv_password_check);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(getBaseContext(), "Successfully signed in with: " + user.getEmail(), Toast.LENGTH_SHORT);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Toast.makeText(getBaseContext(), "Successfully signed out.", Toast.LENGTH_SHORT);
                }
                // ...
            }
        };

        mAuth.addAuthStateListener(mAuthListener);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean isUsernameEmpty = false;
                boolean isPasswordEmpty = false;

                String username = "";
                String userInputStr = username_et.getText().toString();
                if (!userInputStr.equals("")) {
                    username = userInputStr;
                    iv_username_check.setVisibility(VISIBLE);
                } else {
                    isUsernameEmpty = true;
                    iv_username_check.setVisibility(INVISIBLE);
                }

                String password = "";
                final String passwordInputStr = password_et.getText().toString();
                if (!passwordInputStr.equals("")) {
                    password = passwordInputStr;
                    iv_password_check.setVisibility(VISIBLE);
                } else {
                    isPasswordEmpty = true;
                    iv_password_check.setVisibility(INVISIBLE);
                }

                String email = "";
                final String emailInputStr = email_et.getText().toString();
                if (!emailInputStr.equals("")) {
                    email = emailInputStr;
//                    iv_password_check.setVisibility(VISIBLE);
                } else {
//                    isPasswordEmpty = true;
//                    iv_password_check.setVisibility(INVISIBLE);
                }

                if (isUsernameEmpty || isPasswordEmpty) {
                    String requirement = "Please enter a";
                    if (isUsernameEmpty) {
                        requirement += " username";
                    }

                    if (isPasswordEmpty) {
                        if (isUsernameEmpty) {
                            requirement += " and password";
                        } else {
                            requirement += " password";
                        }
                    }

                    requirement += "!";
                    Toast.makeText(getApplicationContext(), requirement, Toast.LENGTH_LONG).show();
                } else {
                    login(username, password, email);
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login(String username, String password, String email) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                    Log.d("MainActivity", "Login was successful!");
                } else {
                    Log.d("MainActivity", "Login failed!");
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Login failed!", Toast.LENGTH_LONG).show();
                }
            }
        });

        mAuth.signInWithEmailAndPassword(email,password);
    }
}