package com.example.encryptaapplication.Activities;


import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.service.autofill.VisibilitySetterAction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.encryptaapplication.Adapters.cntholder;
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

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    private RecyclerView mRecyclerview;
    StorageReference storageReference;
    private DatabaseReference myDatabase;
    private String uid, friend_uid;
    FirebaseRecyclerAdapter<usermodel, cntholder> firebaseRecyclerAdapter;
    private AlertDialog dialog;
    ProgressBar loadingbar;
    private TextView nodatatext;
    private DatabaseReference user_read_flag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_contacts, container, false);
        mRecyclerview = (RecyclerView)view.findViewById(R.id.cnt_recyclerview);
        loadingbar = view.findViewById(R.id.cnt_progress);
        nodatatext = view.findViewById(R.id.nodatatext_cnt);
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference().child("profile_pic");
        uid = current_user.getUid();
        myDatabase = FirebaseDatabase.getInstance().getReference().child("FriendList").child(uid);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerview.setLayoutManager(layoutManager);
        user_read_flag = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("read_flag");
        SetupRecyclerview();
        return view;
    }

    void SetupRecyclerview(){
        myDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    nodatatext.setVisibility(VISIBLE);
                    loadingbar.setVisibility(GONE);
                } else {
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

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<usermodel, cntholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final cntholder holder, final int position, @NonNull usermodel model) {
                myDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child((getRef(position).getKey()));
                final cntholder cntholder = holder;
                myDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            try {
                                String name = dataSnapshot.child("name").getValue().toString();
                                String username = dataSnapshot.child("username").getValue().toString();
                                final String image = dataSnapshot.child("image").getValue().toString();
                                String email = dataSnapshot.child("email").getValue().toString();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(image.endsWith(".jpg")||image.endsWith(".png")||image.endsWith(".jpeg")){
                                            SetProfilePicture(storageReference.child(image),cntholder.getProfileimage());}
                                    }
                                }).start();

                                cntholder.getName().setText(name);
                                cntholder.getUsername().setText(username);

                                cntholder.getName().setText(name);

                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holder.getAction().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog(getRef(position).getKey());
                    }
                });

                holder.getOpenchat_layout().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        user_read_flag.setValue("true");
                        Intent ChatActivity = new Intent(getContext(), ChatActivity.class);
                        ChatActivity.putExtra("friend_id",getRef(position).getKey());
                        getContext().startActivity(ChatActivity);
                    }

                });
                user_read_flag.setValue("false");
                loadingbar.setVisibility(GONE);
            }

            @NonNull
            @Override
            public cntholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.contact_friendlist_row, parent, false);
                return new cntholder(view);
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

    //UnFriend Function
    void UnFriend( String friendkey){

        myDatabase = FirebaseDatabase.getInstance().getReference().child(getContext().getString(R.string.FriendList));

        DatabaseReference d = myDatabase.child(uid).child(friendkey);

        final String frndkey = friendkey;
        d.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                DatabaseReference d2 = myDatabase.child(frndkey).child(uid);
                d2.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(getContext(),"Unfriend Succesfully",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void Dialog(final String friendkey){
        if(dialog!=null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        dialog = new AlertDialog.Builder(getContext()).create();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.unfriend_dialog,null,false);

        Button confirm = view.findViewById(R.id.cnt_confirm_button);
        Button cancel = view.findViewById(R.id.cnt_cancel_button);
        dialog.setCanceledOnTouchOutside(false);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UnFriend(friendkey);
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setView(view);
        dialog.show();
    }
}