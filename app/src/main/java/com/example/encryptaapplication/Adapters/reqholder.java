package com.example.encryptaapplication.Adapters;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.encryptaapplication.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class reqholder extends RecyclerView.ViewHolder {
    CircleImageView profileimage;
    TextView name;
    TextView request_text;
    TextView username;
    ImageButton action;


    public reqholder(@NonNull View itemView) {
        super(itemView);

        profileimage = itemView.findViewById(R.id.req_imageview);
        name = itemView.findViewById(R.id.req_nametextview);
        username = itemView.findViewById(R.id.req_username_textview);
        request_text = itemView.findViewById(R.id.req_text);
        action = itemView.findViewById(R.id.req_button);
    }

    public CircleImageView getProfileimage() {
        return profileimage;
    }

    public TextView getName() {
        return name;
    }

    public TextView getRequest_text() {
        return request_text;
    }

    public TextView getUsername() {
        return username;
    }

    public ImageButton getAction() {
        return action;
    }



}