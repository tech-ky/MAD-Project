package com.sp.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;

import com.sp.mad_project.R;

public class plans_countdown extends AppCompatActivity {

    private TextView timerText;
    private ContentLoadingProgressBar circularProgress;
    private Button stopButton;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 3000; // 3 seconds (change this to whatever time you want)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans_countdown);

        // Initialize UI elements
        timerText = findViewById(R.id.timer_text);
        circularProgress = findViewById(R.id.circular_progress);
        stopButton = findViewById(R.id.stop_button);

        // Start the timer automatically
        startTimer();

        // Stop button event listener
        stopButton.setOnClickListener(v -> stopTimer());
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerUI();
            }

            @Override
            public void onFinish() {
                timerText.setText("0:00");
                circularProgress.setProgress(0);

                // Navigate to exercise1 activity when timer hits 0:00
                Intent intent = new Intent(plans_countdown.this, exercise1.class);
                startActivity(intent); // Start exercise1 activity
                finish(); // Finish the current activity (optional)
            }
        }.start();
    }

    private void updateTimerUI() {
        int seconds = (int) (timeLeftInMillis / 1000);
        timerText.setText(String.format("%d:%02d", seconds / 60, seconds % 60));
        circularProgress.setProgress(seconds);
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        finish(); // Exit activity
    }
}
