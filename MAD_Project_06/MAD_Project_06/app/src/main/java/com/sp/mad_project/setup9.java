package com.sp.mad_project;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class setup9 extends AppCompatActivity {
    private Button nextBtn;
    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup9);

        nextBtn = findViewById(R.id.next_button);
        backBtn = findViewById(R.id.back_button);

        UserInfo databaseHelper = new UserInfo(this);

        // Next Button: Pass the UserInfo object to setup10
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextIntent = new Intent(setup9.this, setup10.class);
                startActivity(nextIntent);
                finish();
            }
        });

        // Back Button: Pass the UserInfo object back to setup8
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backIntent = new Intent(setup9.this, setup8.class);
                 startActivity(backIntent);
                finish();
            }
        });
    }
}
