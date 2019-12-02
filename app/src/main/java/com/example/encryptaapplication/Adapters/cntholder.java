package com.example.encryptaapplication.Adapters;

import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.encryptaapplication.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class cntholder extends RecyclerView.ViewHolder {
    CircleImageView profileimage;
    TextView name;
    TextView request_text;
    TextView username;
    ImageButton action;
    LinearLayout openchat_layout;
    String UID;

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public LinearLayout getOpenchat_layout() {
        return openchat_layout;
    }

    public cntholder(@NonNull View itemView) {
        super(itemView);

        profileimage = itemView.findViewById(R.id.cnt_imageview);
        name = itemView.findViewById(R.id.cnt_nametextview);
        username = itemView.findViewById(R.id.cnt_username_textview);
        request_text = itemView.findViewById(R.id.cnt_text);
        action = itemView.findViewById(R.id.cnt_unfriend_button);
        openchat_layout = itemView.findViewById(R.id.mainclick_layout);
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