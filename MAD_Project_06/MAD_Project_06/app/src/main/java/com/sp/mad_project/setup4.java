package com.sp.mad_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class setup4 extends AppCompatActivity {
    private Button nextBtn;
    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);

        nextBtn = findViewById(R.id.next_button);
        backBtn = findViewById(R.id.back_button);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pass the UserInfo object to setup5
                Intent nextIntent = new Intent(setup4.this, setup5.class);
                startActivity(nextIntent);
            }
        });

        // Handle back button click
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go back to setup3
                Intent backIntent = new Intent(setup4.this, setup3.class);
                startActivity(backIntent);
            }
        });
    }
}
