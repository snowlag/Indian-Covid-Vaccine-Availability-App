package com.example.covidtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class RegisterUser extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;

    //register user form fields
    private EditText Username, Email, Age, Password;
    private Button RegisterButton;
    private TextView LoginPageButton;
    private ProgressBar ProgressbarForRegisteration;

    //firestone instance
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        //create firebase instance
        mAuth = FirebaseAuth.getInstance();

        //configure form fields
        Username = findViewById(R.id.name);
        Email = findViewById(R.id.RegisterEmailAddress);
        Age = findViewById(R.id.age);
        Password = findViewById(R.id.RegisterPassword);

        //configure register button
        RegisterButton = (Button) findViewById(R.id.register_user_button);
        RegisterButton.setOnClickListener(this);

        //configure go to login page button
        LoginPageButton = findViewById(R.id.loginPageButton);
        LoginPageButton.setOnClickListener(this);

        //configure progress bar
        ProgressbarForRegisteration = findViewById(R.id.Register_progressBar);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_user_button:
                RegisterUserInFirebase();
                break;

            case R.id.loginPageButton:
                startActivity(new Intent(this, MainActivity.class));
                break;

            default:
                break;
        }
    }

    public void RegisterUserInFirebase() {
        //extract text
        String username = Username.getText().toString().trim();
        String age = Age.getText().toString().trim();
        String email = Email.getText().toString().trim();
        String password = Password.getText().toString().trim();

        //validation
        if (username.isEmpty()) {
            Username.setError("Name is required");
            Username.requestFocus();
            return;
        }

        if (age.isEmpty()) {
            Age.setError("Age is required");
            Age.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            Email.setError("Email is required");
            Email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email.setError("Please enter valid email address");
            Email.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            Password.setError("Password is required");
            Password.requestFocus();
            return;
        }

        if (password.length() < 6) {
            Password.setError("Please make password above 6 characters");
            Password.requestFocus();
            return;
        }


        //Add user in firebase
        //create user object
        User NewUser = new User(username, Integer.parseInt(age), email);
        //User sign in firebase authentication


        ProgressbarForRegisteration.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //on successful adding of user in firebase authentication
                        if (task.isSuccessful()) {
                            //add the user in firestone
                            try {
                                FirebaseFirestore.getInstance().collection("users")
                                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .set(NewUser)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {
                                                    ProgressbarForRegisteration.setVisibility(View.GONE);
                                                    //toast user
                                                    Toast.makeText(RegisterUser.this, "User registered successfully ,Verification link sent to your email", Toast.LENGTH_SHORT).show();
                                                    //send verification mail
                                                    FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                                                    //go to login screen
                                                    startActivity(new Intent(RegisterUser.this, MainActivity.class));
                                                } else {

                                                    ProgressbarForRegisteration.setVisibility(View.GONE);
//                                            //toast failed
                                                    Toast.makeText(RegisterUser.this, "Failed to register user in firestone", Toast.LENGTH_LONG).show();
                                                }

                                            }
                                        });

                            } catch (Exception e) {
                                e.printStackTrace();

                                Toast.makeText(RegisterUser.this, "Failed to register user in firebase authentication", Toast.LENGTH_LONG).show();
                            }

//
                        } else {

                            ProgressbarForRegisteration.setVisibility(View.GONE);
                            Toast.makeText(RegisterUser.this, "Failed to register user in firebase authentication", Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }
}

