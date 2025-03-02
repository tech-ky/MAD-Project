package com.sp.mad_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class setup6 extends AppCompatActivity {
    private Button nextBtn;
    private ImageButton backBtn;
    private String activityLevel = "";  // String to hold the selected activity level

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup6);
        nextBtn = findViewById(R.id.next_button);
        backBtn = findViewById(R.id.back_button);

        UserInfo databaseHelper = new UserInfo(this);  // Assuming UserInfo is a class that takes Context

        // Handle next button click
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save the selected activity level in the UserInfo object
                databaseHelper.setActivityLevel(activityLevel);

                // Pass the updated UserInfo object to the next setup screen (setup7)
                Intent nextIntent = new Intent(setup6.this, setup7.class);
                 startActivity(nextIntent);
                finish();
            }
        });

        // Handle back button click
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pass the UserInfo object back to setup5
                Intent backIntent = new Intent(setup6.this, setup5.class);
                startActivity(backIntent);
                finish();
            }
        });

        // Find the LinearLayout that contains the checkboxes.
        LinearLayout goalSelectionLayout = findViewById(R.id.activity_selection);

        // Loop through each child of the layout.
        int childCount = goalSelectionLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = goalSelectionLayout.getChildAt(i);
            if (child instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) child;

                // Set an OnCheckedChangeListener on each CheckBox.
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // Only react when a CheckBox is being checked.
                        if (isChecked) {
                            int checkedCount = countCheckedBoxes(goalSelectionLayout);
                            if (checkedCount > 1) {
                                // If more than 1 box would be checked, revert this change.
                                buttonView.setChecked(false);
                                Toast.makeText(setup6.this,
                                        "You can only select 1 option", Toast.LENGTH_SHORT).show();
                            } else {
                                // Update the activityLevel based on the selected checkbox using else if
                                if (buttonView.getId() == R.id.checkbox_option1) {
                                    activityLevel = "low";
                                } else if (buttonView.getId() == R.id.checkbox_option2) {
                                    activityLevel = "moderate";
                                } else if (buttonView.getId() == R.id.checkbox_option3) {
                                    activityLevel = "high";
                                } else if (buttonView.getId() == R.id.checkbox_option4) {
                                    activityLevel = "extreme";
                                }
                            }
                        } else {
                            // If no checkbox is checked, reset the activityLevel
                            activityLevel = "";
                        }
                    }
                });
            }
        }
    }

    // Count how many checkboxes are checked
    private int countCheckedBoxes(LinearLayout layout) {
        int count = 0;
        int childCount = layout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = layout.getChildAt(i);
            if (child instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) child;
                if (checkBox.isChecked()) {
                    count++;
                }
            }
        }
        return count;
    }
}
