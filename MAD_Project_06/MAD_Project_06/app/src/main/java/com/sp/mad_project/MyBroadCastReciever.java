package com.sp.mad_project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.widget.Toast;

public class MyBroadCastReciever extends BroadcastReceiver {
    private MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "ALARM TRIGGERED!", Toast.LENGTH_LONG).show();

        // Vibrate for 5 seconds
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(5000); // Vibrate for 5 seconds
        }

        // Play alarm sound
        Uri alarmSound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.alarm_sound);
        mediaPlayer = MediaPlayer.create(context, alarmSound);
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }
}
