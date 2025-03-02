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

public class exercise1 extends AppCompatActivity {

    private TextView timerText;
    private ContentLoadingProgressBar circularProgress;
    private Button stopButton;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 60000; // 60 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise1);

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