package com.example.miniprayertracker;

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
    private Button submitButton, resetButton;
    private JSONObject prayerTimes;
    private Handler handler = new Handler();
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer_entry);

        welcomeMessageTextView = findViewById(R.id.welcome_message_text_view);
        summaryTextView = findViewById(R.id.summary_text_view);
        fajrCheckbox = findViewById(R.id.fajr_checkbox);
        dhuhrCheckbox = findViewById(R.id.dhuhr_checkbox);
        asrCheckbox = findViewById(R.id.asr_checkbox);
        maghribCheckbox = findViewById(R.id.maghrib_checkbox);
        ishaCheckbox = findViewById(R.id.isha_checkbox);
        submitButton = findViewById(R.id.submit_button);
        resetButton = findViewById(R.id.reset_button);

        welcomeMessageTextView.setText(R.string.welcome_message);

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

        submitButton.setOnClickListener(v -> {
            if (!fajrCheckbox.isChecked() && !dhuhrCheckbox.isChecked() && !asrCheckbox.isChecked() && !maghribCheckbox.isChecked() && !ishaCheckbox.isChecked()) {
                Toast.makeText(PrayerEntryActivity.this, getString(R.string.no_prayer_selected), Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(PrayerEntryActivity.this, SummaryActivity.class);
            intent.putExtra("fajr", fajrCheckbox.isChecked());
            intent.putExtra("dhuhr", dhuhrCheckbox.isChecked());
            intent.putExtra("asr", asrCheckbox.isChecked());
            intent.putExtra("maghrib", maghribCheckbox.isChecked());
            intent.putExtra("isha", ishaCheckbox.isChecked());
            startActivityForResult(intent, SUMMARY_REQUEST_CODE);
            Toast.makeText(PrayerEntryActivity.this, getString(R.string.data_saved), Toast.LENGTH_SHORT).show();
        });

        resetButton.setOnClickListener(v -> {
            fajrCheckbox.setChecked(false);
            dhuhrCheckbox.setChecked(false);
            asrCheckbox.setChecked(false);
            maghribCheckbox.setChecked(false);
            ishaCheckbox.setChecked(false);
            summaryTextView.setText("");
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SUMMARY_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                String resultMessage = data.getStringExtra("resultMessage");
                int score = data.getIntExtra("score", 0);
                summaryTextView.setText(resultMessage + " Your score is: " + score + "%");
            } else if (resultCode == RESULT_CANCELED && data != null) {
                String cancelMessage = data.getStringExtra("cancelMessage");
                summaryTextView.setText(cancelMessage);
            } else {
                summaryTextView.setText(getString(R.string.no_summary_result));
            }
        }
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
}