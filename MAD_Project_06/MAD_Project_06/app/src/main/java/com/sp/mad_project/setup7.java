package com.sp.mad_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class setup7 extends AppCompatActivity {

    private Spinner countrySpinner;
    private EditText postalCodeInput;
    private RadioGroup genderGroup;
    private Button nextBtn;
    private ImageButton backBtn;
    private EditText AgeInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup7);

        // Initialize UI elements
        nextBtn = findViewById(R.id.next_button);
        backBtn = findViewById(R.id.back_button);
        genderGroup = findViewById(R.id.gender_selection);
        countrySpinner = findViewById(R.id.location_input);
        postalCodeInput = findViewById(R.id.postalcode);
        AgeInput = findViewById(R.id.age_input);

        // Spinner Adapter for country selection
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
                this,
                R.layout.custom_spinner,
                R.id.spinner_text,
                getResources().getStringArray(R.array.country_list)
        );
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        countrySpinner.setAdapter(adapter);

        UserInfo databaseHelper = new UserInfo(this);  // Assuming UserInfo is a class that takes Context

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get selected gender
                int selectedId = genderGroup.getCheckedRadioButtonId();
                RadioButton selectedGender = findViewById(selectedId);
                String gender = (selectedGender != null) ? selectedGender.getText().toString() : "Not Selected";

                // Get selected country
                String selectedCountry = countrySpinner.getSelectedItem().toString();

                //Get Age
                String Age = AgeInput.getText().toString();

                // Get postal code
                String postalCode = postalCodeInput.getText().toString().trim();

                // Validate Inputs
                if (selectedId == -1 || postalCode.isEmpty()) {
                    Toast.makeText(setup7.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Save the selected information into UserInfo
                databaseHelper.setGender(gender);
                databaseHelper.setCountry(selectedCountry);
                databaseHelper.setPostalCode(postalCode);
                databaseHelper.setAge(Age);

                // Pass the updated UserInfo object to the next activity (setup8)
                Intent nextIntent = new Intent(setup7.this, setup8.class);
                startActivity(nextIntent);
                finish();
            }
        });

        // Back Button: Pass the UserInfo object back to setup6
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backIntent = new Intent(setup7.this, setup6.class);
                startActivity(backIntent);
                finish();
            }
        });
    }
}
