package com.example.encryptaapplication.Adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.encryptaapplication.Activities.UserSettingsActivity;
import com.example.encryptaapplication.R;
import com.example.encryptaapplication.model.usermodel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFoundAdapter extends RecyclerView.Adapter<UserFoundAdapter.UserHolder> {
    private StorageReference mStorageRef;
    Activity activity;
    ArrayList<usermodel> Data;
    StorageReference storageReference;

    public UserFoundAdapter(Activity activity, ArrayList<usermodel> data){
        this.activity = activity;
        this.Data = data;
        mStorageRef = FirebaseStorage.getInstance().getReference();
        storageReference = mStorageRef.child("profile_pic");

    }


    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity.getBaseContext()).inflate(R.layout.find_friend_row,parent,false);


        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserHolder holder, final int position) {

        holder.name.setText(Data.get(position).getName());
        holder.username.setText("@"+Data.get(position).getUsername());

        new Thread(new Runnable() {
            @Override
            public void run() {
                SetProfilePicture(storageReference.child(Data.get(position).getProfile()),holder.profileimage);
            }
        }).start();

    }

    @Override
    public int getItemCount() {
        return Data.size();
    }

    public class UserHolder extends RecyclerView.ViewHolder{
        CircleImageView profileimage;
        TextView name;
        TextView username;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
            profileimage = itemView.findViewById(R.id.found_imageview);
            name = itemView.findViewById(R.id.found_nametextview);
            username = itemView.findViewById(R.id.username_textview);
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


}