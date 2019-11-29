// Jamil Gonzalez

package com.example.encryptaapplication.Activities;

import com.example.encryptaapplication.Activities.MainActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.encryptaapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class LoginActivity extends AppCompatActivity {

    Button login_btn, forgot_btn, cancel_btn;
    private EditText username, password;
    private FirebaseAuth mAuth;
    private int EMPTYEMAIL=0;
    private int ISAUSERNAME=1;
    private int ISEMAIL =2;
    private DatabaseReference myDatabase;
    AlertDialog dialog;
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


        // cancel takes user back to main page
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        login_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String entered_name = username.getText().toString().toLowerCase();
                final String entered_password = password.getText().toString();

                switch (isValidEmail(entered_name)) {
                    //if username
                    case 1:
                        Dialog();
                        Log.d("EMAIL CHECK","we are here");
                        myDatabase = FirebaseDatabase.getInstance().getReference();
                        Query query = myDatabase.child("Users").orderByChild("username").equalTo(entered_name);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Log.d("EMAIL CHECK","we are here 1.1");
                                if(dataSnapshot.exists()){
                                    String data ="";
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                                    {
                                        Iterable<DataSnapshot> child =   postSnapshot.getChildren();


                                        while (child.iterator().hasNext()){
                                            DataSnapshot dasta =child.iterator().next();
                                            if(dasta.getKey().contentEquals("email")){
                                                data=dasta.getValue().toString();
                                                login(data,entered_password);
                                            }

                                        }

                                    }



                                }else{
                                    if(dialog!=null&dialog.isShowing()){

                                        dialog.dismiss();
                                    }
                                    Toast.makeText(LoginActivity.this, "Incorrect login", Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        break;
                    case 2:
                        Dialog();
                        login(entered_name,entered_password);
                        break;
                    default:
                        if(dialog!=null) {
                            if (dialog.isShowing()) {

                                dialog.dismiss();
                            }
                        }

                        break;
                }

            }
        });

        // forgot takes user to forgot password page
        // TODO: CREATE FORGOT PASSWORD PAGE
        forgot_btn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ResetPassword.class);
                startActivity(intent);
                finish();
            }
        });

    }
    private void login(String email_username,String entered_password){
        if (isValidPassword(entered_password)) {


            mAuth.signInWithEmailAndPassword(email_username, entered_password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //remove not verified after test
                        if (!mAuth.getCurrentUser().isEmailVerified()||mAuth.getCurrentUser().isEmailVerified()) {
                            Toast.makeText(LoginActivity.this, "Signing in...", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(intent);
                            if(dialog!=null) {
                                if (dialog.isShowing()) {

                                    dialog.dismiss();
                                }
                            }
                            finish();
                        } else {
                            if(dialog!=null) {
                                if (dialog.isShowing()) {

                                    dialog.dismiss();
                                }
                            }
                            Toast.makeText(LoginActivity.this, "Please verify your email address", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if(dialog!=null) {
                            if (dialog.isShowing()) {

                                dialog.dismiss();
                            }
                        }
                        Toast.makeText(LoginActivity.this, "Incorrect login", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }

    private void Dialog(){
        if(dialog!=null) {
            if (dialog.isShowing()) {

                dialog.dismiss();
            }
        }
        dialog = new AlertDialog.Builder(this).create();
        View view = LayoutInflater.from(this).inflate(R.layout.loading,null,false);
        dialog.setView(view);
        dialog.show();

    }

    private int isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            Toast.makeText(LoginActivity.this,"Email field empty",Toast.LENGTH_SHORT).show();
            return EMPTYEMAIL;
        } else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()) {
            return ISAUSERNAME;
        }
        // TODO: else if email is in use
        else {
            return ISEMAIL;
        }
    }

    public boolean isValidPassword(String password){

        if(password.length() >= 8 && password.length() <=16){
            return true;
        }else {
            Toast.makeText(LoginActivity.this,"Your password must be between 8 and 16 characters",Toast.LENGTH_SHORT).show();
            return false;
        }
    }



}

