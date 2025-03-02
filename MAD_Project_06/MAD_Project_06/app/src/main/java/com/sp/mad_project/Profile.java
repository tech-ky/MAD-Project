package com.sp.mad_project;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private static final int PICK_IMAGE_REQUEST = 1; // Request code for image selection

    private TableLayout tableLayout;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private String userEmail;

    Button changeProfile, changePassword;
    ImageButton profilePic, goback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        changeProfile = findViewById(R.id.changeprofile);
        changePassword = findViewById(R.id.changepassword);
        goback = findViewById(R.id.goback4);
        profilePic = findViewById(R.id.imageButton4);

        tableLayout = findViewById(R.id.profileTable);
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            userEmail = user.getEmail();
            if (userEmail != null) {
                findUserIdByEmail(userEmail);
            } else {
                Log.e(TAG, "Email is null");
                addRowToTable("Error", "User email is null");
            }
        } else {
            Log.e(TAG, "No user signed in.");
            addRowToTable("Error", "User not signed in");
        }

        // Change Password Button - Sends Password Reset Email
        changePassword.setOnClickListener(view -> {
            if (user != null) {
                auth.sendPasswordResetEmail(user.getEmail())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(Profile.this, "Password reset email sent!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Profile.this, "Failed to send reset email.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Go Back Button - Ends the Activity
        goback.setOnClickListener(view -> finish());

        // Change Profile Picture - Opens Gallery or Camera
        changeProfile.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });
    }

    private void findUserIdByEmail(String email) {
        // Instead of querying directly for email, we query all users under 'email' and then filter by email.
        databaseReference = FirebaseDatabase.getInstance().getReference("email");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean userFound = false;

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String storedEmail = userSnapshot.child("email").getValue(String.class);

                    if (storedEmail != null && storedEmail.equals(email)) {
                        String userId = userSnapshot.getKey();  // User ID from Firebase
                        fetchUserData(userId);
                        userFound = true;
                        break;
                    }
                }

                if (!userFound) {
                    Log.e(TAG, "User ID not found for email: " + email);
                    addRowToTable("Error", "User not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
                addRowToTable("Error", "Failed to load data");
            }
        });
    }

    private void fetchUserData(String userId) {
        databaseReference = FirebaseDatabase.getInstance().getReference("email").child(userId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Handle possible conversion issues for fields that could be numeric but stored as strings
                    addRowToTable("Username", snapshot.child("username").getValue(String.class));
                    addRowToTable("Email", snapshot.child("email").getValue(String.class));

                    // Safe conversion for numerical fields (e.g., age, height, weight)
                    addRowToTable("Age", getValueAsString(snapshot, "age"));
                    addRowToTable("Gender", snapshot.child("gender").getValue(String.class));
                    addRowToTable("Country", snapshot.child("country").getValue(String.class));

                    addRowToTable("Height (cm)", getValueAsString(snapshot, "height"));
                    addRowToTable("Weight (kg)", getValueAsString(snapshot, "weight"));

                    addRowToTable("Activity Level", snapshot.child("Activitylevel").getValue(String.class));
                    addRowToTable("Goals", snapshot.child("Goal").getValue(String.class));
                    addRowToTable("Postal Code", snapshot.child("postalcode").getValue(String.class));
                } else {
                    addRowToTable("Error", "User data not available.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                addRowToTable("Error", "Failed to load data");
            }
        });
    }

    private String getValueAsString(DataSnapshot snapshot, String fieldName) {
        if (snapshot.child(fieldName).exists()) {
            Object value = snapshot.child(fieldName).getValue();
            if (value instanceof Long) {
                return String.valueOf(value); // Safe conversion from Long to String
            } else if (value instanceof String) {
                try {
                    return String.valueOf(Long.parseLong((String) value)); // Convert string to long if needed
                } catch (NumberFormatException e) {
                    return "Invalid number format";
                }
            }
        }
        return "N/A"; // Return default value if the field is not found or invalid
    }


    private void addRowToTable(String label, String value) {
        TableRow row = new TableRow(this);
        TextView labelText = new TextView(this);
        TextView valueText = new TextView(this);

        labelText.setText(label);
        valueText.setText(value != null ? value : "N/A");

        labelText.setPadding(20, 20, 20, 20);
        valueText.setPadding(20, 20, 20, 20);

        row.addView(labelText);
        row.addView(valueText);

        tableLayout.addView(row);
    }

    // Handle Image Selection from Gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            if (selectedImageUri != null) {
                profilePic.setImageURI(selectedImageUri); // Set the image on the ImageButton
            } else {
                Log.e(TAG, "Failed to select image: URI is null");
            }
        }
    }
}
