package com.example.encryptaapplication.Adapters;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;

import com.example.encryptaapplication.Activities.ChatActivity;
import com.example.encryptaapplication.R;
import com.example.encryptaapplication.model.usermodel;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context numContext;
    private List<usermodel> numUsermodel;

    public UserAdapter(Context numContext, List<usermodel> numUsermodel) {
        this.numContext = numContext;
        this.numUsermodel = numUsermodel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(numContext).inflate(R.layout.activity_user_settings, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final usermodel user = numUsermodel.get(position);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                 Intent intent = new Intent(numContext, ChatActivity.class);
                 intent.putExtra("username", user.getName());
                 numContext.startActivity(intent);

              }
            });
    }

    @Override
    public int getItemCount() {
        return numUsermodel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profile_image;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_pic);
        }
    }
}
