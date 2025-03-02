package com.sp.mad_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText usernameInput;
    private EditText passwordInput;
    private TextView signupText;
    private Button loginBtn;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        loginBtn = findViewById(R.id.signup_btn);
        signupText = findViewById(R.id.signup_text);
        databaseHelper = new DatabaseHelper(this);

        signupText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d("SignupTextClick", "Signup Text Clicked!");
                Intent intent = new Intent(MainActivity.this,SignupActivity.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String user = usernameInput.getText().toString().trim();
                String pass = passwordInput.getText().toString().trim();
                if(user.isEmpty() || pass.isEmpty()){
                    Toast.makeText(MainActivity.this,"Please enter username and password", Toast.LENGTH_SHORT).show();
                }
                else {
                    String loggedInUserName = databaseHelper.checkLogin(user,pass);
                    if(loggedInUserName!=null)
                    {
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        intent.putExtra("USERNAME", loggedInUserName);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(MainActivity.this,"Invalid Username", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    } // Close onCreate
} // Close class
