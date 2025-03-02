package com.sp.mad_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class setup2 extends AppCompatActivity {
    private ImageButton backBtn;
    private Button nextBtn;
    private EditText name_input;
    private Context context;
    private UserInfo databaseHelper;  // Create the UserInfo object
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);

        backBtn = findViewById(R.id.back_button);
        nextBtn = findViewById(R.id.next_button);
        name_input = findViewById(R.id.first_name_input);
        databaseHelper = new UserInfo(this);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(setup2.this, setup1.class);
                startActivity(intent);
                finish();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the username from the EditText field
                String username = name_input.getText().toString().trim();

                if (username.isEmpty()) {
                    // Show an error message if the username is empty
                    Toast.makeText(context, "Please enter a username", Toast.LENGTH_SHORT).show();
                } else {
                    // Store the username in the DatabaseHelper
                    databaseHelper.setUsername(username);

                    // Proceed to the next activity
                    Intent nextIntent = new Intent(setup2.this, setup3.class);
                    startActivity(nextIntent);
                    finish(); // Close the current activity
                    name_input.getText().clear();
                    finish();
                }
            }
        });
    }
}