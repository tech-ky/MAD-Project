package com.sp.mad_project.placeholder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sp.mad_project.R;
import com.sp.mad_project.BurnCalDbHelper;

import java.util.HashMap;
import java.util.Map;

public class PlanFragment extends Fragment {

    private static final String TAG = "PlanFragment";
    private static final String PREFS_NAME = "WorkoutPrefs";
    private static final String PREFS_TIMESTAMP_KEY = "LastButtonHideTimestamp";

    private int totalCalories = 0;
    private TextView caloriesBurn;
    private BurnCalDbHelper dbHelper;

    private FirebaseAuth auth;
    private int userPoints = 0;
    private String userKey = "";

    public PlanFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        dbHelper = new BurnCalDbHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plan, container, false);

        Button workout1 = view.findViewById(R.id.workout1);
        Button workout2 = view.findViewById(R.id.workout2);
        Button workout3 = view.findViewById(R.id.workout3);
        Button workout4 = view.findViewById(R.id.workout4);
        caloriesBurn = view.findViewById(R.id.total_calories);

        fetchUserData();

        totalCalories = dbHelper.getTotalCalories();
        caloriesBurn.setText(totalCalories + " cal");

        restoreButtonVisibility(workout1, workout2, workout3, workout4);

        workout1.setOnClickListener(v -> handleWorkoutClick(workout1, 8, 20, "Workout 1 Completed"));
        workout2.setOnClickListener(v -> handleWorkoutClick(workout2, 20, 40, "Workout 2 Completed"));
        workout3.setOnClickListener(v -> handleWorkoutClick(workout3, 45, 90, "Workout 3 Completed"));
        workout4.setOnClickListener(v -> handleWorkoutClick(workout4, 100, 170, "Workout 4 Completed"));

        return view;
    }

    private void handleWorkoutClick(Button button, int points, int calories, String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        button.setVisibility(View.GONE);

        totalCalories += calories;
        caloriesBurn.setText(totalCalories + " cal");

        dbHelper.saveTotalCalories(totalCalories);
        updateUserPoints(points);

        // Save the current timestamp to SharedPreferences when a button is hidden
        SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(PREFS_TIMESTAMP_KEY, System.currentTimeMillis());
        editor.apply();
    }

    private void restoreButtonVisibility(Button workout1, Button workout2, Button workout3, Button workout4) {
        SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long lastHideTimestamp = prefs.getLong(PREFS_TIMESTAMP_KEY, 0);

        // Check if 1 day has passed since the last time the button was hidden
        if (System.currentTimeMillis() - lastHideTimestamp >= 86400000) { // 86400000 milliseconds = 1 day
            workout1.setVisibility(View.VISIBLE);
            workout2.setVisibility(View.VISIBLE);
            workout3.setVisibility(View.VISIBLE);
            workout4.setVisibility(View.VISIBLE);
        }
    }

    private void fetchUserData() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        String userEmail = user.getEmail();
        if (userEmail == null) return;

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("email");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean emailFound = false;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String emailInDatabase = userSnapshot.child("email").getValue(String.class);
                    if (userEmail.equals(emailInDatabase)) {
                        userPoints = userSnapshot.child("point").getValue(Integer.class);
                        userKey = userSnapshot.getKey();
                        emailFound = true;
                        break;
                    }
                }
                if (!emailFound) {}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateUserPoints(int pointsToAdd) {
        if (userKey.isEmpty()) return;

        int newPoints = userPoints + pointsToAdd;
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("email").child(userKey);

        Map<String, Object> updates = new HashMap<>();
        updates.put("point", newPoints);

        userRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> userPoints = newPoints)
                .addOnFailureListener(e -> {});
    }
}
