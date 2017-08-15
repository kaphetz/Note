package com.example.kienpt.note.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.kienpt.note.R;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView txtData = new TextView(this);
        setContentView(txtData);

        txtData.setText("DEMOOOOOOOOOOOOO");
    }
}
