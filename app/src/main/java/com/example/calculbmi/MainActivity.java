package com.example.calculbmi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LifeCycle";
    private TextView prayerTimesTextView;
    private JSONObject prayerTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate() called");

        prayerTimesTextView = findViewById(R.id.prayer_times_text_view);
        Button startButton = findViewById(R.id.start_button);
        Button exitButton = findViewById(R.id.exit_button);

        fetchPrayerTimes();

        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PrayerEntryActivity.class);
            if (prayerTimes != null) {
                intent.putExtra("prayerTimes", prayerTimes.toString());
            }
            startActivity(intent);
        });

        exitButton.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, getString(R.string.exit_message), Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void fetchPrayerTimes() {
        PrayerTimesFetcher.fetchPrayerTimes(this, new PrayerTimesFetcher.PrayerTimesResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                prayerTimes = response;
                displayPrayerTimes(response);
            }

            @Override
            public void onError(String message) {
                prayerTimesTextView.setText(message);
            }
        });
    }

    private void displayPrayerTimes(JSONObject timings) {
        try {
            String fajr = timings.getString("Fajr");
            String dhuhr = timings.getString("Dhuhr");
            String asr = timings.getString("Asr");
            String maghrib = timings.getString("Maghrib");
            String isha = timings.getString("Isha");

            String prayerTimesText = "Fajr: " + fajr + "\n"
                    + "Dhuhr: " + dhuhr + "\n"
                    + "Asr: " + asr + "\n"
                    + "Maghrib: " + maghrib + "\n"
                    + "Isha: " + isha;

            prayerTimesTextView.setText(prayerTimesText);

        } catch (Exception e) {
            prayerTimesTextView.setText("Error displaying prayer times");
        }
    }

    // Lifecycle methods (onStart, onResume, onPause, onStop, onDestroy) remain the same
}