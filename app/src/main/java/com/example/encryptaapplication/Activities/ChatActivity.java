package com.example.encryptaapplication.Activities;

import android.media.audiofx.Visualizer;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.encryptaapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    private DatabaseReference reference1,reference2, deletion_policy, other_user_read_flag;
    private ArrayList<String> reference2_messages = new ArrayList<>();
    String uid,friendid;
    String reference = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        layout = findViewById(R.id.layout1);
        layout_2 = findViewById(R.id.layout2);
        sendButton = findViewById(R.id.sendButton);
        messageArea = findViewById(R.id.messageArea);
        scrollView = findViewById(R.id.scrollView);

        //getting current uid
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        uid = current_user.getUid();

        //getting friend id from the contact fragment
        friendid = getIntent().getStringExtra("friend_id");

        reference1 = FirebaseDatabase.getInstance().getReference().child("Messages").child(uid+"_"+friendid);
        reference2 = FirebaseDatabase.getInstance().getReference().child("Messages").child(friendid+"_"+uid);

        final HashMap<String, String> map = new HashMap<String, String>();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String messageText = messageArea.getText().toString();
                if(!messageText.equals("")){

                    map.put("message", messageText);
                    map.put("user", uid);//UserDetails.username);
                    reference1.push().setValue(map);
                    reference = reference2.push().getKey();
                    reference2.child(reference).setValue(map);
                    reference2_messages.add(reference);
                    messageArea.setText("");
                }

                // enacts the deletion policy while sending a new message
                deletion_policy = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("deletion_policy");
                other_user_read_flag = FirebaseDatabase.getInstance().getReference().child("Users").child(friendid).child("read_flag");

                if (other_user_read_flag.toString().compareTo("true") == 0)
                    enact_deleting_policy();

                other_user_read_flag.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String flag = dataSnapshot.getValue().toString().trim();
                        if (flag.compareTo("true") == 0) {
                            enact_deleting_policy();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });



        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String,Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                String message = map.get("message").toString();
                String userName = map.get("user").toString();

                if(userName.equals(uid)){
                    addMessageBox(message, 1);
                }
                else {
                    addMessageBox(message, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void addMessageBox(String message, int type){
        TextView textView = new TextView(this);
        textView.setText(message);
        textView.setTextSize(20);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);

        //lp2.weight = 7.0f;
        lp2.setMargins(20,20,20,20);
        if(type == 1) {
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.bubble_in);
        }
        else{
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.bubble_out);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    public void delete_messages() {
        while (!reference2_messages.isEmpty()) {
            reference2.child(reference2_messages.get(0)).removeValue();
            reference2_messages.remove(0);
        }
    }

    public void enact_deleting_policy() {
        deletion_policy.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                String deletepol = dataSnapshot.getValue(String.class);
                int time;
                long timecountinMilliseconds;

                if (deletepol != null) {
                    if (deletepol.compareTo("Instant") == 0) {
                        // prob need to implement a 'read' flag
                        time = 5;
                        timecountinMilliseconds = time * 1000;

                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        delete_messages();

                                    }
                                },
                                timecountinMilliseconds);

                    } else if (deletepol.compareTo("5 min") == 0) {
                        time = 300;
                        timecountinMilliseconds = time * 1000;
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        reference2.removeValue();
                                    }
                                },
                                timecountinMilliseconds
                        );
                    } else if (deletepol.compareTo("24 hours") == 0) {
                        time = 1440;
                        timecountinMilliseconds = time * 1000;
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        reference2.removeValue();
                                    }
                                },
                                timecountinMilliseconds
                        );
                    } else {
                        // do not delete messages
                    }
                } else {
                    Toast.makeText(ChatActivity.this, "Err: in deletion policy", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}