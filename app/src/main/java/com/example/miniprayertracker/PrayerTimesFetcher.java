package com.example.miniprayertracker;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class PrayerTimesFetcher {

    private static final String API_URL = "https://api.aladhan.com/v1/timingsByCity?city=Monastir&country=Tunisia&method=8";

    public interface PrayerTimesResponseListener {
        void onResponse(JSONObject response);
        void onError(String message);
    }

    public static void fetchPrayerTimes(Context context, PrayerTimesResponseListener listener) {
        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, API_URL, null,
                response -> {
                    try {
                        listener.onResponse(response.getJSONObject("data").getJSONObject("timings"));
                    } catch (JSONException e) {
                        listener.onError("Error parsing prayer times: " + e.getMessage());
                    }
                },
                error -> listener.onError("Error fetching prayer times: " + error.getMessage())
        );

        queue.add(jsonObjectRequest);
    }
}