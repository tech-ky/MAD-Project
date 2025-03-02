package com.sp.mad_project;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.Toast;

public class setup3 extends AppCompatActivity {
    private Button nextBtn;
    private ImageButton backBtn;
    private String reason = "";  // String to hold the selected goals

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        nextBtn = findViewById(R.id.next_button);
        backBtn = findViewById(R.id.back_button);
        UserInfo databaseHelper = new UserInfo(this);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Save the selected goals into the UserInfo object
                databaseHelper.setGoal(reason);

                Intent nextIntent = new Intent(setup3.this, setup4.class);
                startActivity(nextIntent);
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(setup3.this, setup2.class);
                startActivity(intent);
                finish();
            }
        });

        // Find the LinearLayout that contains the checkboxes.
        LinearLayout goalSelectionLayout = findViewById(R.id.goal_selection);

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
                            if (checkedCount > 3) {
                                // If more than 3 boxes would be checked, revert this change.
                                buttonView.setChecked(false);
                                Toast.makeText(setup3.this, "You can only select up to 3 options", Toast.LENGTH_SHORT).show();
                            } else {
                                // Add the selected checkbox label to the 'reason' variable
                                reason += checkBox.getText().toString() + ", ";
                            }
                        } else {
                            // Remove the checkbox label from the 'reason' string if unchecked
                            reason = reason.replace(checkBox.getText().toString() + ", ", "");
                        }
                    }
                });
            }
        }
    }

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
