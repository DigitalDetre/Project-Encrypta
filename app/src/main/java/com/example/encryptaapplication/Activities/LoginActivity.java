// Jamil Gonzalez

package com.example.encryptaapplication.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.encryptaapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private Button login_btn, forgot_btn, cancel_btn;
    private EditText username, password;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login_btn = (Button) findViewById(R.id.login_btn);
        forgot_btn = (Button) findViewById(R.id.forgot_btn);
        cancel_btn = (Button) findViewById(R.id.cancel_btn);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // cancel takes user back to main page
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        // login takes user to his page (TODO: JOSH)
        login_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String entered_name = username.getText().toString();
                String entered_password = password.getText().toString();
                mAuth.signInWithEmailAndPassword(entered_name, entered_password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (mAuth.getCurrentUser().isEmailVerified()) {
                                Toast.makeText(LoginActivity.this, "Signing in...", Toast.LENGTH_SHORT).show();

                                // TODO: CHANGE MAIN ACTIVITY
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                                return;
                            } else {
                                Toast.makeText(LoginActivity.this, "Please verify your email address", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Incorrect login", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        // forgot takes user to forgot password page
        // TODO: CREATE FORGOT PASSWORD PAGE
        forgot_btn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        }

    }


