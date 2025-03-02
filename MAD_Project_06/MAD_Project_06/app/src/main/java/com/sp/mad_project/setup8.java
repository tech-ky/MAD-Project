package com.sp.mad_project;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class setup8 extends AppCompatActivity {
    private Button nextBtn;
    private ImageButton backBtn;
    private EditText heightInput, weightInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup8);

        nextBtn = findViewById(R.id.next_button);
        backBtn = findViewById(R.id.back_button);
        heightInput = findViewById(R.id.height_input);
        weightInput = findViewById(R.id.weight_input);

        UserInfo databaseHelper = new UserInfo(this);  // Assuming UserInfo is a class that takes Context

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String weight = weightInput.getText().toString().trim();
                String height = heightInput.getText().toString().trim();

                if (weight.isEmpty() || height.isEmpty()) {
                    Toast.makeText(setup8.this, "Please fill in both weight and height", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Save the height and weight in the UserInfo object
                databaseHelper.setHeight(height);
                databaseHelper.setWeight(weight);

                // Pass the updated UserInfo object to the next activity (setup9)
                Intent nextIntent = new Intent(setup8.this, setup9.class);
                 startActivity(nextIntent);
                finish();
            }
        });

        // Back Button: Pass the UserInfo object back to setup7
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backIntent = new Intent(setup8.this, setup7.class);
                 startActivity(backIntent);
                finish();
            }
        });
    }
}
