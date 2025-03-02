package com.sp.mad_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

public class setup7 extends AppCompatActivity {

    private Spinner countrySpinner;
    private Button nextBtn;
    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup7);

        nextBtn = findViewById(R.id.next_button);
        backBtn = findViewById(R.id.back_button);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(setup7.this, setup8.class);
                startActivity(intent);
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(setup7.this, setup6.class);
                startActivity(intent);
            }
        });

        // Find the Spinner (ensure the Spinner XML does NOT include android:entries)
        countrySpinner = findViewById(R.id.location_input);

        // Create an ArrayAdapter using your custom layout.
        // The third argument (R.id.spinner_text) tells the adapter which TextView inside your custom layout to populate.
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
                this,
                R.layout.custom_spinner,  // This layout's root is a RelativeLayout.
                R.id.spinner_text,        // The TextView inside custom_spinner.xml
                getResources().getStringArray(R.array.country_list)
        );

        // --- Troubleshooting Tip ---
        // To check whether the problem is with your custom dropdown layout,
        // try using the default Android dropdown layout:
        // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //
        // If the crash goes away, then the issue is with custom_spinner_dropdown.xml.
        // Otherwise, if you want to use your custom dropdown, use the line below:
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);

        // Set the adapter on the Spinner.
        countrySpinner.setAdapter(adapter);
    }
}


