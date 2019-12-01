package com.example.encryptaapplication.Activities;


import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.encryptaapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MessagesActivity extends AppCompatActivity{

    private TextView window_text, policy_text;
    private Spinner spinner;
    private Button change_btn;
    CharSequence prompt;
    DatabaseReference databaseReference;
    FirebaseUser current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        prompt = databaseReference.toString();
        current_user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user.getUid()).child("deletion_policy");
        window_text = findViewById(R.id.window_text);
        policy_text = findViewById(R.id.policy_text);
        spinner = findViewById(R.id.spinner);
        change_btn = findViewById(R.id.change_btn);



        change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String spinnerItem;
                spinnerItem = spinner.getSelectedItem().toString().trim();


                databaseReference.setValue(spinnerItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user.getUid()).child("deletion_policy");
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ref.setValue(spinnerItem);
                                Toast.makeText(MessagesActivity.this, "Deletion policy changed to " + spinnerItem, Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                });

            }
        });
    }

}
