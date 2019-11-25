package com.example.encryptaapplication.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.encryptaapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar mToolBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
        mToolBar = (Toolbar) findViewById(R.id.home_page_toolbar);

        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Encrypta");

    }
   // Checks if the user is already logged in if not go back to the main page
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){

            Intent mainIntent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         getMenuInflater().inflate(R.menu.home_menu,menu);

         return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         super.onOptionsItemSelected(item);

         if(item.getItemId() == R.id.home_logout){

             FirebaseAuth.getInstance().signOut();

             Intent mainIntent = new Intent(HomeActivity.this, MainActivity.class);
             startActivity(mainIntent);
             finish();
         }
         return true;
    }
}
