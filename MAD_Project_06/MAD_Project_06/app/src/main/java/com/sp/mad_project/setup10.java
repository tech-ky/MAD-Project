package com.sp.mad_project;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class setup10 extends AppCompatActivity {

    Button let_go_button;
    UserInfo databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup10);

        let_go_button = findViewById(R.id.lets_go_button);
        databaseHelper = new UserInfo(this);

        let_go_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if the email and password are available

                // Create the new user in the database
                databaseHelper.createNewUserToDB();

                // Navigate to the MainActivity after successful account creation
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

                // Optionally reset the user data after successful creation
                databaseHelper.resetUserData();
                finish();
            }
        });
    }
}
