package com.sp.mad_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.sp.mad_project.placeholder.PlanFragment;

public class plan_start extends AppCompatActivity {
    private Button startBtn;
    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_start);
        startBtn = findViewById(R.id.start_button);
        backBtn = findViewById(R.id.back_button);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(plan_start.this, com.sp.mad_project.plans_countdown.class);
                startActivity(intent);
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(plan_start.this, PlanFragment.class);
                startActivity(intent);
            }
        });
    }
}