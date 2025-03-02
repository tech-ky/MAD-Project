package com.sp.mad_project;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartSplash extends AppCompatActivity {

    private Handler handler = new Handler();
    private Runnable runnable;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_splash); // Ensure you have a layout set

        mediaPlayer = MediaPlayer.create(this, R.raw.sound_effect1); // Replace 'sound_effect' with your file name
        mediaPlayer.setVolume(1.0f, 1.0f);
        // Start playing the sound
        mediaPlayer.start();

        runnable = new Runnable() {
            @Override
            public void run() {
                // Check sign-in status using FirebaseAuth
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                Intent intent;
                if (currentUser != null) {
                    // User is logged in
                    intent = new Intent(StartSplash.this, MainActivity.class);
                } else {
                    // User is not logged in
                    intent = new Intent(StartSplash.this, Login.class);
                }
                startActivity(intent);
                finish(); // Finish splash activity
            }
        };

        // Execute the runnable after 5 seconds
        handler.postDelayed(runnable, 5000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Release media player resources when the activity is destroyed
        }
    }
}
