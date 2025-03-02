package com.sp.mad_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class setup5 extends AppCompatActivity {
    private Button nextBtn;
    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup5);

        nextBtn = findViewById(R.id.next_button);
        backBtn = findViewById(R.id.back_button);

        // Retrieve the UserInfo object passed from setup4
        Intent intent = getIntent();

        // Handle next button click
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pass the UserInfo object to setup6
                Intent nextIntent = new Intent(setup5.this, setup6.class);
                startActivity(nextIntent);
            }
        });

        // Handle back button click
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go back to setup4
                Intent backIntent = new Intent(setup5.this, setup4.class);
                startActivity(backIntent);
            }
        });
    }
}
