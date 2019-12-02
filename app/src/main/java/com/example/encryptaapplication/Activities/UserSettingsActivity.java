package com.example.encryptaapplication.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.example.encryptaapplication.R;
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
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

// Displays the user settings page
public class UserSettingsActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    private CircleImageView mProfilePicture;
    private TextView mName;

    private Button mProfilePictureBtn,btn_changename, messages_btn;
    private static final int Gallery_pick = 1;

    private StorageReference mStorageRef;
    private TextView mUsername;
    private String profile_pic_name;
    ProgressBar picloadbar;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        mProfilePicture = (CircleImageView) findViewById(R.id.profile_pic);
        mName = (TextView) findViewById(R.id.display_name);
        mUsername = (TextView) findViewById(R.id.display_username);
        picloadbar = (ProgressBar)findViewById(R.id.pic_loadbar);
        btn_changename = (Button)findViewById(R.id.btn_name);
        mProfilePictureBtn = (Button) findViewById(R.id.update_ppic);
        messages_btn = (Button) findViewById(R.id.messages);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mStorageRef = FirebaseStorage.getInstance().getReference();

        String current_uid = mCurrentUser.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String username = dataSnapshot.child("username").getValue().toString();
                    String image = dataSnapshot.child("image").getValue().toString();
                    String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                    mName.setText(name);
                    mUsername.setText(username);
                    final StorageReference ref = mStorageRef.child("profile_pic").child(image);
                    SetProfilePicture(ref);

                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mProfilePictureBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(gallery, "Select Image"), Gallery_pick);
            }
        });
        btn_changename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog();
            }
        });

        messages_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MessagesActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Gallery_pick && resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                File ff = new File(resultUri.toString());
                String extension = ff.getAbsolutePath().substring(ff.getAbsolutePath().lastIndexOf("."));
                profile_pic_name = mUsername.getText().toString()+"_profile_image"+extension;
                Log.d("profile_pic_name",profile_pic_name);
                final StorageReference filepath = mStorageRef.child("profile_pic").child(profile_pic_name);

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            mUserDatabase.child("image").setValue(profile_pic_name).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    SetProfilePicture(filepath);
                                }
                            });
                        }else{
                            Toast.makeText(UserSettingsActivity.this,"error not uploading",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    void SetProfilePicture(StorageReference reference){
        picloadbar.setVisibility(View.VISIBLE);
        final long ONE_MEGABYTE = 1024 * 1024;
        reference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytesPrm) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytesPrm, 0, bytesPrm.length);
                mProfilePicture.setImageBitmap(bmp);
                picloadbar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(UserSettingsActivity.this,"unable to load profile pic",Toast.LENGTH_SHORT).show();
                picloadbar.setVisibility(View.GONE);
                mProfilePicture.setImageResource(R.mipmap.default_icon);
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
        View view = LayoutInflater.from(this).inflate(R.layout.changename_layout,null,false);
        final EditText newname = view.findViewById(R.id.changename_edittext);
        Button confirm = view.findViewById(R.id.confirm_button);
        Button cancel = view.findViewById(R.id.cancel_button);
        dialog.setCanceledOnTouchOutside(false);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(newname.getText().toString())) {
                    Toast.makeText(UserSettingsActivity.this,"name  field empty",Toast.LENGTH_SHORT).show();
                } else {
                    mUserDatabase.child("name").setValue(newname.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(UserSettingsActivity.this,"name is changed",Toast.LENGTH_SHORT).show();
                            mName.setText(newname.getText().toString());
                            dialog.dismiss();
                        }
                    });
                }
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