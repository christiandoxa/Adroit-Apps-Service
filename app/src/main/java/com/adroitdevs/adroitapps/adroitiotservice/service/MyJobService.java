package com.adroitdevs.adroitapps.adroitiotservice.service;

import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;

import com.adroitdevs.adroitapps.adroitiotservice.VolleyCallback;
import com.adroitdevs.adroitapps.adroitiotservice.VolleySingleton;
import com.adroitdevs.adroitapps.adroitiotservice.model.TokenPrefrences;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by rexchris on 15/09/17.
 */

public class MyJobService extends JobService {
    public static final String COUNTDOWN_BR = "com.adroitdevs.adroitapps.adroitiotservice";
    String url = "http://192.168.88.59:3000/history";
    Date currentTime = Calendar.getInstance().getTime();
    SimpleDateFormat formatOld = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
    Intent bi = new Intent(COUNTDOWN_BR);
    CountDownTimer cdt = null;
    JobParameters mJobParameters;
    String id = "";

    @Override
    public boolean onStartJob(JobParameters job) {
        mJobParameters = job;
        getRemaining(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result) {

            }

            @Override
            public void onSuccessJsonObject(JSONObject result) {
                try {
                    Log.d("JobService", result.getString("tgl_selesai").substring(0, 10) + "T" + result.getString("tgl_selesai").substring(11, result.getString("tgl_selesai").length() - 7) + "Z");
                    Date resultDate = formatOld.parse(result.getString("tgl_selesai").substring(0, 10) + "T" + result.getString("tgl_selesai").substring(11, result.getString("tgl_selesai").length() - 7) + "Z");
                    id = result.getString("id_jemuran");
                    long seconds = (resultDate.getTime() - currentTime.getTime());
                    int dateInSeconds = Integer.parseInt(String.valueOf(seconds));
                    broadcastingID();
                     cdt = new CountDownTimer(dateInSeconds, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            int hours = (int) TimeUnit.SECONDS.toHours(millisUntilFinished / 1000);
                            int minutes = (int) TimeUnit.SECONDS.toMinutes(millisUntilFinished / 1000) - (hours * 60);
                            bi.putExtra("hours", hours);
                            bi.putExtra("minutes", minutes);
                            sendBroadcast(bi);
                        }

                        @Override
                        public void onFinish() {
                            updateStatus();
                            Log.d("JobService", "countdown finish");
                            Intent done = new Intent(COUNTDOWN_BR);
                            done.putExtra("updateList", true);
                            sendBroadcast(done);
                            jobFinished(mJobParameters, true);
                        }
                    };
                    cdt.start();
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return true;
    }

    private void broadcastingID() {
        Intent intentID = new Intent(COUNTDOWN_BR);
        intentID.putExtra("id", id);
        sendBroadcast(intentID);
    }

    private void updateStatus() {
        StringRequest request = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    Log.d("JobService", String.valueOf(res.getBoolean("status")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("JobService", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> body = new HashMap<>();
                body.put("id", id);
                return body;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> head = new HashMap<>();
                head.put("Authorization", "Bearer " + TokenPrefrences.getToken(getBaseContext()));
                return head;
            }
        };
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(request);
    }

    private void getRemaining(final VolleyCallback callback) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray result = response.getJSONArray("result");
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject history = result.getJSONObject(i);
                        if (history.getString("status").equals("belum kering")) {
                            callback.onSuccessJsonObject(history);
                            break;
                        }
                    }
                } catch (JSONException e) {
                    Log.e("JobService", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("JobService", error.toString());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> head = new HashMap<>();
                head.put("Authorization", "Bearer " + TokenPrefrences.getToken(getBaseContext()));
                return head;
            }
        };
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(request);
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (cdt != null) {
            cdt.cancel();
        }
        Log.d("JobService", "JobStopped");
        return false;
    }
}
