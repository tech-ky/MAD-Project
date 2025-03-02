package com.sp.mad_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class leadershipboard extends AppCompatActivity {
    RecyclerView UserListView;
    MainAdapter mainAdapter;
    ImageButton goback;
    List<UserInfo1> userList = new ArrayList<>(); // Updated to UserInfo1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leadershipboard);

        // Set up the go-back button functionality
        goback = findViewById(R.id.gobackmorefragment6);
        goback.setOnClickListener(view -> finish());  // Close the current activity

        // Initialize RecyclerView
        UserListView = findViewById(R.id.AllUser);
        UserListView.setLayoutManager(new LinearLayoutManager(this));

        // Load user data from Firebase
        loadUserData();
    }

    private void loadUserData() {
        // Firebase reference to 'email' node
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("email");

        // Retrieve all users
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();  // Clear previous data
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String username = userSnapshot.child("username").getValue(String.class);
                    Integer points = userSnapshot.child("point").getValue(Integer.class);
                    String userKey = userSnapshot.getKey();

                    if (username != null && points != null && userKey != null) {
                        UserInfo1 user = new UserInfo1(username, points, userKey); // Updated to UserInfo1
                        userList.add(user);  // Add user to the list
                    }
                }

                // Sort the users by points in descending order
                Collections.sort(userList, (user1, user2) -> Integer.compare(user2.getPoints(), user1.getPoints()));

                // If the list is empty, show a message
                if (userList.isEmpty()) {
                    Toast.makeText(leadershipboard.this, "No users found!", Toast.LENGTH_SHORT).show();
                }

                // Log the sorted data
                for (UserInfo1 user : userList) {
                    System.out.println("User: " + user.getUsername() + ", Points: " + user.getPoints());
                }

                // Set up the adapter and bind the data to the RecyclerView
                mainAdapter = new MainAdapter(userList);
                UserListView.setAdapter(mainAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(leadershipboard.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start listening to changes in Firebase
        if (mainAdapter != null) {
            mainAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Stop listening to changes
        if (mainAdapter != null) {
            mainAdapter.notifyDataSetChanged();
        }
    }
}
