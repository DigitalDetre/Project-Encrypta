// Jamil Gonzalez

package com.example.encryptaapplication.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.encryptaapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpActivity extends AppCompatActivity {

    private EditText editTextName,editTextEmail,editTextPassword,editTextPassword2;
    private Button btnSingup;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextName = (EditText) findViewById(R.id.idSignupName);
        editTextEmail = (EditText) findViewById(R.id.idSignupEmail);
        editTextPassword = (EditText) findViewById(R.id.idSignupPassword);
        editTextPassword2 = (EditText) findViewById(R.id.idRewritePassword);

        btnSingup = (Button) findViewById(R.id.idbuttonSignup);

        mAuth = FirebaseAuth.getInstance();

        btnSingup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = editTextEmail.getText().toString();
                final String name = editTextName.getText().toString();

                if(isValidName(name) && isValidEmail(email) && isValidPassword()){

                    String password = editTextPassword.getText().toString();
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Toast.makeText(SignUpActivity.this, "Account created!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            });
                    // TODO: transition to the login page
                }

                // TODO: else if one of the fields is wrong, keep displaying activity until its fixed
            }
        });

    }
    private boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            Toast.makeText(SignUpActivity.this,"Email field empty",Toast.LENGTH_SHORT).show();
            return false;
        } else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()) {
            Toast.makeText(SignUpActivity.this,"Invalid email",Toast.LENGTH_SHORT).show();
            return false;
        }
        // TODO: else if email is in use
        else {
            return true;
        }
    }
    public boolean isValidPassword(){

        String password,password2;

        password = editTextPassword.getText().toString();
        password2 = editTextPassword2.getText().toString();


        if(password.equals(password2)){
            if(password.length() >= 8 && password.length() <=16){
               return true;
            }else {
                Toast.makeText(SignUpActivity.this,"Your password must be between 8 and 16 characters",Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(SignUpActivity.this,"Your password does not match",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean isValidName(String name){
        // TODO: NEEDS QUERY DATA BASE FOR USERNAMES TO MAKE SURE THE ONE ENTERED IS UNIQUE
        // TODO: MIN NUMBER OF CHAR FOR USERNAME?

        if (name.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Username field empty", Toast.LENGTH_SHORT).show();
        }
        return !name.isEmpty();
    }
}
