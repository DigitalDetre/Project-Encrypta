// Jamil Gonzalez

package com.example.encryptaapplication.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpActivity extends AppCompatActivity {

    private EditText editTextEmail,editTextPassword,editTextPassword2;
    Button btnSingup, cancel_btn;
    private FirebaseAuth mAuth;
    private ProgressDialog mSignupProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextEmail = (EditText) findViewById(R.id.idSignupEmail);
        editTextPassword = (EditText) findViewById(R.id.idSignupPassword);
        editTextPassword2 = (EditText) findViewById(R.id.idRewritePassword);
        cancel_btn = (Button) findViewById(R.id.cancel_btn);
        btnSingup = (Button) findViewById(R.id.idbuttonSignup);
        mAuth = FirebaseAuth.getInstance();

        mSignupProgress = new ProgressDialog(this);


        // cancel takes user back to main page
        cancel_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        final ActionCodeSettings actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        // URL you want to redirect back to. The domain (www.example.com) for this
                        // URL must be whitelisted in the Firebase Console.
                        .setUrl("encrypta-fd737.firebaseapp.com")
                        // This must be true
                        .setHandleCodeInApp(true)
                        .setAndroidPackageName(
                                "com.example.encryptaapplication",
                                true, /* installIfNotAvailable */
                                "12"    /* minimumVersion */)
                        .build();


        btnSingup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = editTextEmail.getText().toString();
                final String password = editTextPassword.getText().toString();

                if (isValidEmail(email) && isValidPassword()) {
                    mSignupProgress.setTitle("Creating a new Account");
                    mSignupProgress.setMessage("Please wait a few second ...");
                    mSignupProgress.setCanceledOnTouchOutside(false);
                    mSignupProgress.show();

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mSignupProgress.dismiss();
                                            Toast.makeText(SignUpActivity.this, "Email verification sent", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            mSignupProgress.hide();
                                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(SignUpActivity.this, "Account already exists", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

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

}
