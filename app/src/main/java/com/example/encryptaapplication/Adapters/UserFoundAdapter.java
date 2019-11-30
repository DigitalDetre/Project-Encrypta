package com.example.encryptaapplication.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.encryptaapplication.Activities.UserSettingsActivity;
import com.example.encryptaapplication.R;
import com.example.encryptaapplication.model.usermodel;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFoundAdapter extends RecyclerView.Adapter<UserFoundAdapter.UserHolder> {
    private StorageReference mStorageRef;
    Activity activity;
    ArrayList<usermodel> Data;
    StorageReference storageReference;
    private DatabaseReference myDatabase;
    private String uid;
    private int RequestState=0;
    private int WorkStatus=0;
    private AlertDialog dialog;
    private AlertDialog respond_dialog;

    public UserFoundAdapter(Activity activity, ArrayList<usermodel> data){
        this.activity = activity;
        this.Data = data;
        mStorageRef = FirebaseStorage.getInstance().getReference();
        storageReference = mStorageRef.child("profile_pic");
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        uid = current_user.getUid();
    }


    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity.getBaseContext()).inflate(R.layout.find_friend_row,parent,false);


        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserHolder holder, final int position) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(Data.get(position).getProfile().endsWith(".jpg")||Data.get(position).getProfile().endsWith(".png")||Data.get(position).getProfile().endsWith(".jpeg")){
                    SetProfilePicture(storageReference.child(Data.get(position).getProfile()),holder.profileimage);}
            }
        }).start();

        //first condition
        if(uid.contentEquals(Data.get(position).getParentID())){
            holder.SendReq.setVisibility(View.GONE);
            holder.request_text.setVisibility(View.GONE);
        }
        else {

            // if we are already friends
            myDatabase = FirebaseDatabase.getInstance().getReference().child("FriendList").child(uid).child(Data.get(position).getParentID());
            myDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        holder.SendReq.setImageResource(R.drawable.ic_friends_grey);
                        holder.request_text.setText("Unfriend");
                        WorkStatus = 1;

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });

            // if he already sent me the request


            myDatabase = FirebaseDatabase.getInstance().getReference().child("FriendRequest").child(uid);
            myDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(Data.get(position).getParentID())){
                        holder.SendReq.setImageResource(R.drawable.ic_friends);
                        holder.request_text.setText("Requested");
                        WorkStatus = 2;
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



            // Check if i already sent the request
            myDatabase = FirebaseDatabase.getInstance().getReference().child("FriendRequest").child(Data.get(position).getParentID());
            myDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(uid)){
                        holder.SendReq.setImageResource(R.drawable.ic_add_usergrey);
                        holder.request_text.setText("Cancel");
                        WorkStatus = 3;

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        holder.name.setText(Data.get(position).getName());
        holder.username.setText("@"+Data.get(position).getUsername());






        holder.SendReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {


                if(!uid.contentEquals(Data.get(position).getParentID())){
                    //if user is not his friend
                    if(WorkStatus==0) {
                        myDatabase = FirebaseDatabase.getInstance().getReference().child("FriendRequest").child(Data.get(position).getParentID()).child(uid);
                        HashMap<String, String> request = new HashMap<>();
                        request.put("RequestType", "GET");

                        myDatabase.setValue(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    holder.SendReq.setImageResource(R.drawable.ic_add_usergrey);
                                    holder.request_text.setText("Cancel");
                                    RequestState = 1;
                                }
                            }
                        });
                    }
                    //if user is his friend
                    else if(WorkStatus==1){

                        Dialog(Data.get(position).getParentID(),holder);

                    }
                    //if user already sent him request
                    else if(WorkStatus==2){

                        respond(Data.get(position).getParentID());

                    }


                    else if(WorkStatus==3){

                        myDatabase = FirebaseDatabase.getInstance().getReference().child("FriendRequest");
                        myDatabase.child(Data.get(position).getParentID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                holder.SendReq.setImageResource(R.drawable.ic_add_userblue);
                                holder.request_text.setText("Send Request");
                                WorkStatus=0;

                            }
                        });



                    }
                }else{
                    Toast.makeText(activity,"CODE TO OPEN OWN PROFIle",Toast.LENGTH_SHORT).show();
                    //Show His OWN PROFILE
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return Data.size();
    }

    public class UserHolder extends RecyclerView.ViewHolder{
        CircleImageView profileimage;
        TextView name;
        TextView request_text;
        TextView username;
        ImageButton SendReq;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
            profileimage = itemView.findViewById(R.id.found_imageview);
            name = itemView.findViewById(R.id.found_nametextview);
            username = itemView.findViewById(R.id.username_textview);
            SendReq = itemView.findViewById(R.id.send_req_button);
            request_text=itemView.findViewById(R.id.request_text);
        }
    }



    void SetProfilePicture(StorageReference reference, final CircleImageView imageView){

        final long ONE_MEGABYTE = 1024 * 1024;
        reference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytesPrm) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytesPrm, 0, bytesPrm.length);
                imageView.setImageBitmap(bmp);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(activity,"unable to load profile pic",Toast.LENGTH_SHORT).show();

                imageView.setImageResource(R.mipmap.default_icon);
            }
        });
    }


    // UNFRIEND DIALOG START
    private void Dialog(final String friendkey, final UserHolder holder){
        if(dialog!=null) {
            if (dialog.isShowing()) {

                dialog.dismiss();
            }
        }
        dialog = new AlertDialog.Builder(activity).create();
        View view = LayoutInflater.from(activity).inflate(R.layout.unfriend_dialog,null,false);

        Button confirm = view.findViewById(R.id.cnt_confirm_button);
        Button cancel = view.findViewById(R.id.cnt_cancel_button);
        dialog.setCanceledOnTouchOutside(false);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UnFriend(friendkey,holder);
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

    void UnFriend(String friendkey, final UserHolder holder){

        myDatabase = FirebaseDatabase.getInstance().getReference().child(activity.getString(R.string.FriendList));

        DatabaseReference d = myDatabase.child(uid).child(friendkey);

        final String  frndkey = friendkey;
        d.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


                DatabaseReference d2 = myDatabase.child(frndkey).child(uid);
                d2.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(activity,"Unfriend Succesfully",Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
                            holder.SendReq.setImageResource(R.drawable.ic_add_userblue);
                            holder.request_text.setText("Send Request");
                            WorkStatus=0;

                        }
                    }
                });



            }
        });


    }

//UNFRIED DIALOG END



    private void respond(final String friendkey){
        if( respond_dialog!=null) {
            if (respond_dialog.isShowing()) {

                respond_dialog.dismiss();
            }
        }
        respond_dialog = new AlertDialog.Builder(activity).create();
        View view = LayoutInflater.from(activity).inflate(R.layout.acceptordelete,null,false);
        Button accept = view.findViewById(R.id.accept_button);
        final Button delete = view.findViewById(R.id.delete_button);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Two steps (first)
                myDatabase = FirebaseDatabase.getInstance().getReference().child(activity.getString(R.string.FriendList));

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
                                    notifyDataSetChanged();
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
                notifyDataSetChanged();
            }
        });

        respond_dialog.setView(view);
        respond_dialog.show();

    }

    void DeleteReq(String friendkey){
        myDatabase = FirebaseDatabase.getInstance().getReference().child("FriendRequest");
        myDatabase.child(uid).child(friendkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                respond_dialog.dismiss();
                notifyDataSetChanged();
            }
        });

    }

}