package com.sp.mad_project;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Voucher extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private int userPoints = 0;
    private String userKey = ""; // Stores the Firebase key of the authenticated use
    ImageButton goBackButton;
    Button buyFairpriceButton, buyGrabButton;
    private static final String TAG = "VoucherActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);

        goBackButton = findViewById(R.id.gobackmorefragment6);
        buyFairpriceButton = findViewById(R.id.buy_fairprice);
        buyGrabButton = findViewById(R.id.buy_grab);

        auth = FirebaseAuth.getInstance();

        // Fetch the user's points when activity starts
        getUserPointsByEmail();

        // Go back button logic
        goBackButton.setOnClickListener(v -> finish());

        // Buy Fairprice button logic
        buyFairpriceButton.setOnClickListener(v -> checkAndBuyVoucher("Fairprice", 400));

        // Buy Grab button logic
        buyGrabButton.setOnClickListener(v -> checkAndBuyVoucher("Grab", 100));
    }

    private void getUserPointsByEmail() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Log.e(TAG, "User not authenticated.");
            return;
        }

        String userEmail = user.getEmail();
        if (userEmail == null) {
            Log.e(TAG, "User email is null, cannot fetch points.");
            return;
        }

        Log.d(TAG, "Fetching points for user with email: " + userEmail);

        mDatabase = FirebaseDatabase.getInstance().getReference("email");

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean emailFound = false;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String emailInDatabase = userSnapshot.child("email").getValue(String.class);
                    Log.d(TAG, "Checking user with email: " + emailInDatabase);

                    if (userEmail.equals(emailInDatabase)) {
                        userPoints = userSnapshot.child("point").getValue(Integer.class);
                        userKey = userSnapshot.getKey(); // Store user's database key
                        Log.d(TAG, "User points retrieved: " + userPoints);
                        emailFound = true;
                        break; // Exit loop once found
                    }
                }

                if (!emailFound) {
                    Log.e(TAG, "User email not found in database.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
            }
        });
    }

    private void checkAndBuyVoucher(String voucherName, int pointsRequired) {
        if (userKey.isEmpty()) {
            Log.e(TAG, "Error: User data not loaded yet.");
            Toast.makeText(this, "Error: User data not loaded.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Checking if user has enough points for " + voucherName);

        if (userPoints >= pointsRequired) {
            // Deduct points and update in Firebase
            userPoints -= pointsRequired;

            Log.d(TAG, "User has enough points. Deducting " + pointsRequired + " points. New balance: " + userPoints);

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("email").child(userKey);
            userRef.child("point").setValue(userPoints).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Points updated successfully. User points: " + userPoints);
                    Toast.makeText(Voucher.this, voucherName + " voucher purchased!", Toast.LENGTH_SHORT).show();

                    // Exit the activity after successful purchase
                    finish();
                } else {
                    Log.e(TAG, "Failed to update points in Firebase.");
                    Toast.makeText(Voucher.this, "Failed to update points.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e(TAG, "Not enough points to buy " + voucherName + " voucher. Current points: " + userPoints);
            Toast.makeText(Voucher.this, "Not enough points to buy " + voucherName + " voucher.", Toast.LENGTH_SHORT).show();
        }
    }
}
