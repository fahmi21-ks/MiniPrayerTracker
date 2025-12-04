package com.example.calculbmi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SummaryActivity extends AppCompatActivity {

    private int score = 0;
    private String summaryText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        TextView summaryDetailsTextView = findViewById(R.id.summary_details_text_view);
        TextView scoreResultTextView = findViewById(R.id.score_result_text_view);
        Button calculateScoreButton = findViewById(R.id.calculate_score_button);
        Button validateButton = findViewById(R.id.validate_return_button);
        Button cancelButton = findViewById(R.id.cancel_return_button);

        Intent intent = getIntent();
        boolean fajr = intent.getBooleanExtra("fajr", false);
        boolean dhuhr = intent.getBooleanExtra("dhuhr", false);
        boolean asr = intent.getBooleanExtra("asr", false);
        boolean maghrib = intent.getBooleanExtra("maghrib", false);
        boolean isha = intent.getBooleanExtra("isha", false);

        StringBuilder summaryBuilder = new StringBuilder(getString(R.string.prayers_completed) + "\n");
        int completedCount = 0;
        if (fajr) { summaryBuilder.append("- ").append(getString(R.string.fajr)).append("\n"); completedCount++; }
        if (dhuhr) { summaryBuilder.append("- ").append(getString(R.string.dhuhr)).append("\n"); completedCount++; }
        if (asr) { summaryBuilder.append("- ").append(getString(R.string.asr)).append("\n"); completedCount++; }
        if (maghrib) { summaryBuilder.append("- ").append(getString(R.string.maghrib)).append("\n"); completedCount++; }
        if (isha) { summaryBuilder.append("- ").append(getString(R.string.isha)).append("\n"); completedCount++; }
        summaryText = summaryBuilder.toString();
        summaryDetailsTextView.setText(summaryText);

        int finalCompletedCount = completedCount;
        calculateScoreButton.setOnClickListener(v -> {
            score = (int) (((double) finalCompletedCount / 5.0) * 100);
            scoreResultTextView.setText(getString(R.string.completion_score, score));
        });

        validateButton.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("resultMessage", getString(R.string.well_done));
            resultIntent.putExtra("score", score);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        cancelButton.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("cancelMessage", getString(R.string.operation_cancelled));
            setResult(RESULT_CANCELED, resultIntent);
            finish();
        });
    }
}