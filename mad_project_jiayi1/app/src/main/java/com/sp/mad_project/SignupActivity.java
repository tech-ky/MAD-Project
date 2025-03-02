package com.sp.mad_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {
    private EditText usernameInput;
    private EditText passwordInput;
    private Button signupBtn;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup); // Use the correct layout for SignupActivity

        // Initialize views
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        signupBtn = findViewById(R.id.signup_btn);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Set the click listener for signup button
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = usernameInput.getText().toString().trim();
                String pass = passwordInput.getText().toString().trim();

                // Validate inputs
                if (user.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                } else {
                    // Attempt to insert data into the database
                    if (databaseHelper.insertData(user, pass)) {
                        Toast.makeText(SignupActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        intent.putExtra("USERNAME", user);
                        startActivity(intent);
                        finish(); // Close the signup activity
                    } else {
                        Toast.makeText(SignupActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
