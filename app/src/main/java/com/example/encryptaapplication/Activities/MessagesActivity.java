package com.example.encryptaapplication.Activities;


import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.encryptaapplication.R;


public class MessagesActivity extends AppCompatActivity{

    private TextView window_text, policy_text;
    private Spinner spinner;
    private Button change_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        window_text = findViewById(R.id.window_text);
//        policy_text = findViewById(R.id.policy_text);
        spinner = findViewById(R.id.spinner);
        change_btn = findViewById(R.id.change_btn);
    }
}
