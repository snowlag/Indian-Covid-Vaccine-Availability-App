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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView RegisterButton, ForgotPasswordButton;
    private FirebaseAuth mAuth;
    private EditText Email, Password;
    private ProgressBar LoginProgressbar;
    private Button LoginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create firebase instance
        mAuth = FirebaseAuth.getInstance();


        //configure register button
        RegisterButton = (TextView) findViewById(R.id.RegisterPageButton);
        RegisterButton.setOnClickListener(this);

        //configure form fields
        Email = findViewById(R.id.LoginEmailAddress);
        Password = findViewById(R.id.LoginPassword);

        //configure forget password button
        ForgotPasswordButton = findViewById(R.id.ForgotPasswordButton);
        ForgotPasswordButton.setOnClickListener(this);

        //configure login button
        LoginButton = findViewById(R.id.LoginButton);
        LoginButton.setOnClickListener(this);

        //configure firebase instance
        mAuth = FirebaseAuth.getInstance();

        //configure progress bar
        LoginProgressbar = findViewById(R.id.LoginProgressBar);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.RegisterPageButton:
//              open register page
                startActivity(new Intent(this, RegisterUser.class));
                break;

            case R.id.LoginButton:
                //login user
                LoginUser();
                break;
            case R.id.ForgotPasswordButton:

                ForgotUserPassword();
                break;
            default:
                break;
        }


    }

    void ForgotUserPassword() {
        //extract text
        String email = Email.getText().toString().trim();
        //validation
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
        LoginProgressbar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Password reset link sent", Toast.LENGTH_LONG).show();
                            LoginProgressbar.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to sent to reset link", Toast.LENGTH_LONG).show();
                            LoginProgressbar.setVisibility(View.GONE);
                        }
                    }
                });


    }


    void LoginUser() {
        //extract text
        String email = Email.getText().toString().trim();
        String password = Password.getText().toString().trim();

        //validation
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

        //login user
        LoginProgressbar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verify the email
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user.isEmailVerified()) {
                                LoginProgressbar.setVisibility(View.GONE);
                                startActivity(new Intent(MainActivity.this, vaccination.class));
                            } else {
                                //send verification mail
                                user.sendEmailVerification();
                                Toast.makeText(MainActivity.this, "Email verification link is send to your email", Toast.LENGTH_LONG).show();
                                LoginProgressbar.setVisibility(View.GONE);
                            }

                        } else {
                            //show error message
                            Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_LONG).show();
                            LoginProgressbar.setVisibility(View.GONE);
                        }
                    }
                });

    }
}