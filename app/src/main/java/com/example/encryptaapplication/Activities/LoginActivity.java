// Jamil Gonzalez

package com.example.encryptaapplication.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.encryptaapplication.R;

public class LoginActivity extends AppCompatActivity {

    private Button createAnAccount;
    private Button signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        createAnAccount = (Button) findViewById(R.id.createAnAccount);
        signIn = (Button) findViewById(R.id.signIn);

        createAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        }
    }


