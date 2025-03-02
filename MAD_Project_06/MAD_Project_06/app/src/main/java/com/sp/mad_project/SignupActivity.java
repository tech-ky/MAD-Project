package com.sp.mad_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {
    private EditText emailInput;
    private EditText passwordInput;
    private EditText ConfirmInput;
    private Button signupBtn;
    private FirebaseAuth mAuth;
    private UserInfo databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        ConfirmInput = findViewById(R.id.confirm_password_input);
        signupBtn = findViewById(R.id.signup_btn);

        // Initialize UserInfo object
        databaseHelper = new UserInfo(this);

        // Set click listener for the signup button
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();
                String confirm = ConfirmInput.getText().toString().trim();

                // Validate email and password
                if (!isValidEmail(email)) {
                    Toast.makeText(SignupActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 12 || confirm.length() < 12) {
                    Toast.makeText(SignupActivity.this, "Password must be at least 12 characters long", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (confirm.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please set password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (confirm.isEmpty() != password.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Passwords do not match. Please re-enter", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create user in Firebase
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Set the email and password in the UserInfo object
                                    databaseHelper.setEmail(email);  // Store email in the UserInfo object
                                    databaseHelper.setPassword(password);  // Store password in the UserInfo object

                                    // Pass the UserInfo object to the next activity
                                    Intent intent = new Intent(SignupActivity.this, setup1.class);
                                    startActivity(intent);

                                    // Close signup activity
                                    finish();
                                } else {
                                    // Firebase signup failed
                                    Toast.makeText(SignupActivity.this, "Something Went Wrong :(" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
    // Method to validate email format
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
