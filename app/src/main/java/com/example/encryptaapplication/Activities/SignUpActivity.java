// Jamil Gonzalez

package com.example.encryptaapplication.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.encryptaapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.functions.FirebaseFunctions;
import com.virgilsecurity.android.common.callback.OnGetTokenCallback;
import com.virgilsecurity.android.ethree.interaction.EThree;
import com.virgilsecurity.common.callback.OnResultListener;

import java.util.HashMap;
import java.util.Map;


public class SignUpActivity extends AppCompatActivity {

    private EditText editTextEmail,editTextPassword,editTextPassword2,edittextUsername;
    Button btnSingup, cancel_btn;
    private FirebaseAuth mAuth;
    private ProgressDialog mSignupProgress;
    private DatabaseReference myDatabase;
    private boolean userexists=false;
    // Fetch Virgil JWT token from Firebase function
    OnGetTokenCallback tokenCallback = new OnGetTokenCallback() {

        @NotNull
        @Override public String onGetToken() {
            Map<String, String> data =
                    (Map<String, String>) FirebaseFunctions.getInstance()
                            .getHttpsCallable("getVirgilJwt")
                            .call()
                            .getResult()
                            .getData();

            return data.get("token");
        }
    };

    OnResultListener<EThree> initializeListener = new OnResultListener<EThree>() {

        @Override public void onSuccess(EThree result) {
            // Init done!
            // Save the eThree instance
        }

        @Override public void onError(@NotNull Throwable throwable) {
            // Error handling
        }
    };

    // Initialize EThree SDK with JWT token from Firebase Function
    EThree.initialize(context, tokenCallback).addCallback(initializeListener);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextEmail = (EditText) findViewById(R.id.idSignupEmail);
        editTextPassword = (EditText) findViewById(R.id.idSignupPassword);
        editTextPassword2 = (EditText) findViewById(R.id.idRewritePassword);
        edittextUsername = (EditText) findViewById(R.id.idSignupUsername);
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


        edittextUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

            }
        });

        edittextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                myDatabase = FirebaseDatabase.getInstance().getReference();
                Query query = myDatabase.child("Users").orderByChild("username").equalTo(charSequence.toString());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            userexists=true;
                            Toast.makeText(SignUpActivity.this,"USername exist",Toast.LENGTH_SHORT).show();
                            edittextUsername.setTextColor(Color.RED);


                        }else{
                            userexists = false;
                            edittextUsername.setTextColor(Color.GREEN);


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {



            }
        });



        btnSingup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!userexists){

                    final String email = editTextEmail.getText().toString();
                    final String password = editTextPassword.getText().toString();

                    if (isValidEmail(email) && isValidPassword()) {
                        mSignupProgress.setTitle("Creating a new Account");
                        mSignupProgress.setMessage("This may take a few seconds ...");
                        mSignupProgress.setCanceledOnTouchOutside(false);
                        mSignupProgress.show();

                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {


                                if (task.isSuccessful()) {
//
//                                //extra code by developer sampat sharma
//                                FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
//                                String uid = current_user.getUid();
//                                myDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
//
//                                HashMap<String, String> userMap = new HashMap<>();
//                                userMap.put("email", email);
//                                userMap.put("name", "Display Name");
//                                userMap.put("username", edittextUsername.getText().toString().toLowerCase());
//                                userMap.put("image", "default");
//                                userMap.put("thumb_image", "default");
//
//
//                                myDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if (task.isSuccessful()) {
//                                            mSignupProgress.dismiss();
//                                            Toast.makeText(SignUpActivity.this, "Email verification sent", Toast.LENGTH_SHORT).show();
//                                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                                            startActivity(intent);
//                                            finish();
//                                        }
//                                    }
//                                });


                                    //end extra code


                                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                                                String uid = current_user.getUid();
                                                myDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                                                HashMap<String, String> userMap = new HashMap<>();
                                                userMap.put("email", email);
                                                userMap.put("name", "Display Name");
                                                userMap.put("username", edittextUsername.getText().toString().toLowerCase());
                                                userMap.put("image", "default");
                                                userMap.put("thumb_image", "default");
                                                myDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()) {
                                                            mSignupProgress.dismiss();
                                                            Toast.makeText(SignUpActivity.this, "Email verification sent", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    }
                                                });

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

                }else {
                    Toast.makeText(SignUpActivity.this, "Change Username", Toast.LENGTH_SHORT).show();


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