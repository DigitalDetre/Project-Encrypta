package com.example.encryptaapplication.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.encryptaapplication.Adapters.UserFoundAdapter;
import com.example.encryptaapplication.R;
import com.example.encryptaapplication.model.usermodel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class AddFriends extends Activity {
    private DatabaseReference myDatabase;
    RecyclerView mRecyclerview ;
    EditText search_edittext;
    ImageButton searchbutton;
    private AlertDialog dialog;
    ArrayList<usermodel> founduser = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.addfriends);

        search_edittext  = (EditText)findViewById(R.id.friend_edittext);
        searchbutton  = (ImageButton) findViewById(R.id.friend_search);
        mRecyclerview = (RecyclerView)findViewById(R.id.friend_recyclerview);
        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(search_edittext.getText().toString())) {
                    Toast.makeText(AddFriends.this,"username field empty",Toast.LENGTH_SHORT).show();

                }else{

                    Dialog();
                    myDatabase = FirebaseDatabase.getInstance().getReference();
                    Query query = myDatabase.child("Users").orderByChild("username").equalTo(search_edittext.getText().toString().toLowerCase());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d("EMAIL CHECK","we are here 1.1");
                            if(dataSnapshot.exists()){
                                founduser = new ArrayList<>();
                                String data ="";
                                String name ="";
                                String email ="";
                                String username ="";
                                String profile_image ="";
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                                {
                                    Iterable<DataSnapshot> child =   postSnapshot.getChildren();


                                    while (child.iterator().hasNext()){
                                        DataSnapshot dasta =child.iterator().next();
                                        if(dasta.getKey().contentEquals("email")){
                                            data=dasta.getValue().toString();
                                            Log.d("DATA email",data);
                                            email = data;
                                        }
                                        if(dasta.getKey().contentEquals("name")){
                                            data=dasta.getValue().toString();
                                            Log.d("DATA name",data);
                                            name = data;

                                        }
                                        if(dasta.getKey().contentEquals("image")){
                                            data=dasta.getValue().toString();
                                            Log.d("DATA image",data);
                                            profile_image = data;
                                        }
                                        if(dasta.getKey().contentEquals("username")){
                                            data=dasta.getValue().toString();
                                            Log.d("DATA username",data);
                                            username = data;
                                        }

                                    }
                                }

                                founduser.add(new usermodel(name,username,profile_image,email));
                                SetupRecyclerView();
                                if(dialog!=null&dialog.isShowing()){ dialog.dismiss(); }

                            }else{
                                if(dialog!=null&dialog.isShowing()){ dialog.dismiss(); }
                                Toast.makeText(AddFriends.this, "No UserFound", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            if(dialog!=null&dialog.isShowing()){

                                dialog.dismiss();
                            }

                        }
                    });

                }
            }
        });

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

    void SetupRecyclerView(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerview.setLayoutManager(layoutManager);
        UserFoundAdapter friendadapter = new UserFoundAdapter(this,founduser);
        mRecyclerview.setAdapter(friendadapter);

    }

}