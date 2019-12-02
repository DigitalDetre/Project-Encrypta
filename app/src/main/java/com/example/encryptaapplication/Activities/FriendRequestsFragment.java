package com.example.encryptaapplication.Activities;


import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.encryptaapplication.Adapters.UserFoundAdapter;
import com.example.encryptaapplication.Adapters.reqholder;
import com.example.encryptaapplication.R;
import com.example.encryptaapplication.model.usermodel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.recyclerview.widget.RecyclerView.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendRequestsFragment extends Fragment {

    private RecyclerView mRecyclerview;
    StorageReference storageReference;
    private DatabaseReference myDatabase;
    private String uid;
    FirebaseRecyclerAdapter<usermodel, reqholder> firebaseRecyclerAdapter;
    private AlertDialog dialog;
    ProgressBar loadingbar;
    TextView nodatatext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_friend_requests, container, false);

        mRecyclerview = (RecyclerView)view.findViewById(R.id.fr_recyclerview);
        loadingbar = view.findViewById(R.id.req_progress);
        nodatatext = view.findViewById(R.id.nodatatext);

        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        storageReference =FirebaseStorage.getInstance().getReference().child("profile_pic");
        uid = current_user.getUid();
        myDatabase = FirebaseDatabase.getInstance().getReference().child("FriendRequest").child(uid);
        LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerview.setLayoutManager(layoutManager);

        getReq_2();
        return view;
    }

    private void getReq_2() {
        myDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    nodatatext.setVisibility(VISIBLE);
                    loadingbar.setVisibility(GONE);
                }else {
                    nodatatext.setVisibility(GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerOptions<usermodel> options =
                new FirebaseRecyclerOptions.Builder<usermodel>()
                        .setQuery(myDatabase, usermodel.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<usermodel, reqholder>(options) {
            @Override
            public void onError(@NonNull DatabaseError error) {
                Log.d("ERROR",error.getMessage().toString());
                super.onError(error);
            }

            @Override
            protected void onBindViewHolder(@NonNull final reqholder holder, final int position, @NonNull usermodel model) {
                Log.d("TAG","tagtatgatata");
                myDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(getRef(position).getKey());

                myDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Log.d( "CHECK ","extracting user data");
                            try {
                                String name = dataSnapshot.child("name").getValue().toString();
                                String username = dataSnapshot.child("username").getValue().toString();
                                final String image = dataSnapshot.child("image").getValue().toString();
                                String email = dataSnapshot.child("email").getValue().toString();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(image.endsWith(".jpg")||image.endsWith(".png")||image.endsWith(".jpeg")){
                                            SetProfilePicture(storageReference.child(image),holder.getProfileimage());}
                                    }
                                }).start();

                                holder.getName().setText(name);
                                holder.getUsername().setText(username);

                                holder.getName().setText(name);

                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holder.getAction().setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        respond(getRef(position).getKey());
                    }
                });

                loadingbar.setVisibility(GONE);
            }

            @NonNull
            @Override
            public reqholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.request_row, parent, false);

                return new reqholder(view);
            }
        };
        mRecyclerview.setAdapter(firebaseRecyclerAdapter);
    }

    void SetProfilePicture(StorageReference reference, final CircleImageView mProfilePicture){
        final long ONE_MEGABYTE = 1024 * 1024;
        reference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytesPrm) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytesPrm, 0, bytesPrm.length);
                mProfilePicture.setImageBitmap(bmp);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getContext(),"unable to load profile pic",Toast.LENGTH_SHORT).show();

                mProfilePicture.setImageResource(R.mipmap.default_icon);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    private void respond(final String friendkey){
        if(dialog!=null) {
            if (dialog.isShowing()) {

                dialog.dismiss();
            }
        }
        dialog = new AlertDialog.Builder(getContext()).create();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.acceptordelete,null,false);
        final EditText newname = view.findViewById(R.id.changename_edittext);
        Button accept = view.findViewById(R.id.accept_button);
        final Button delete = view.findViewById(R.id.delete_button);
        dialog.setCanceledOnTouchOutside(false);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Two steps (first)
                myDatabase = FirebaseDatabase.getInstance().getReference().child(getContext().getString(R.string.FriendList));

                DatabaseReference d = myDatabase.child(uid).child(friendkey);
                HashMap<String, String> userMap = new HashMap<>();
                userMap.put("Status","Friends");

                d.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        DatabaseReference d2 = myDatabase.child(friendkey).child(uid);
                        HashMap<String, String> userMap2 = new HashMap<>();
                        userMap2.put("Status","Friends");

                        d2.setValue(userMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    DeleteReq(friendkey);
                                }
                            }
                        });
                    }
                });
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteReq(friendkey);
                firebaseRecyclerAdapter.notifyDataSetChanged();
            }
        });
        dialog.setView(view);
        dialog.show();
    }

    void DeleteReq(String friendkey){
        myDatabase = FirebaseDatabase.getInstance().getReference().child("FriendRequest");
        myDatabase.child(uid).child(friendkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                firebaseRecyclerAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
    }
}