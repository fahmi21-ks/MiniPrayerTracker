package com.example.calculbmi;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PrayerEntryActivity extends AppCompatActivity {

    private static final int SUMMARY_REQUEST_CODE = 1;

    private CheckBox fajrCheckbox, dhuhrCheckbox, asrCheckbox, maghribCheckbox, ishaCheckbox;
    private TextView welcomeMessageTextView, summaryTextView;
    private JSONObject prayerTimes;
    private Handler handler = new Handler();
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer_entry);

        // ... (findViewById setup)

        String prayerTimesString = getIntent().getStringExtra("prayerTimes");
        if (prayerTimesString != null) {
            try {
                prayerTimes = new JSONObject(prayerTimesString);
            } catch (JSONException e) {
                Toast.makeText(this, "Error parsing prayer times", Toast.LENGTH_SHORT).show();
            }
        }

        updatePrayerCheckboxes();
        scheduleAthanPlayback();

        // ... (button listeners)
    }

    private void updatePrayerCheckboxes() {
        if (prayerTimes == null) return;

        try {
            fajrCheckbox.setEnabled(isPrayerTimePassed(prayerTimes.getString("Fajr")));
            dhuhrCheckbox.setEnabled(isPrayerTimePassed(prayerTimes.getString("Dhuhr")));
            asrCheckbox.setEnabled(isPrayerTimePassed(prayerTimes.getString("Asr")));
            maghribCheckbox.setEnabled(isPrayerTimePassed(prayerTimes.getString("Maghrib")));
            ishaCheckbox.setEnabled(isPrayerTimePassed(prayerTimes.getString("Isha")));
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    private boolean isPrayerTimePassed(String prayerTime) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date prayerDate = sdf.parse(prayerTime);
        Date currentDate = new Date();
        return currentDate.after(prayerDate);
    }

    private void scheduleAthanPlayback() {
        if (prayerTimes == null) return;

        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    checkAndPlayAthan(prayerTimes.getString("Fajr"));
                    checkAndPlayAthan(prayerTimes.getString("Dhuhr"));
                    checkAndPlayAthan(prayerTimes.getString("Asr"));
                    checkAndPlayAthan(prayerTimes.getString("Maghrib"));
                    checkAndPlayAthan(prayerTimes.getString("Isha"));
                    updatePrayerCheckboxes();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                handler.postDelayed(this, 60000); // Check every minute
            }
        });
    }

    private void checkAndPlayAthan(String prayerTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = sdf.format(new Date());

        if (currentTime.equals(prayerTime)) {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            mediaPlayer = MediaPlayer.create(this, R.raw.athan);
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // ... (rest of the class)
}
