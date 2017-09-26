package com.adroitdevs.adroitapps.adroitiotservice.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    JSONArray cdtsJSON;
    List<CountDownTimer> cdts = new ArrayList<>();
    JobParameters mJobParameters;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null && intent.hasExtra("idCount") && cdtsJSON != null && !cdts.isEmpty()) {
                for (int i = 0; i < cdtsJSON.length(); i++) {
                    try {
                        JSONObject cdtJ = cdtsJSON.getJSONObject(i);
                        if (cdtJ.getString("deviceID").equals(intent.getStringExtra("idCount"))) {
                            CountDownTimer cdtCancel = cdts.get(cdtJ.getInt("listIndex"));
                            cdtCancel.cancel();
                            cdts.remove(cdtJ.getInt("listIndex"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    @Override
    public boolean onStartJob(JobParameters job) {
        getBaseContext().registerReceiver(broadcastReceiver, new IntentFilter(COUNTDOWN_BR));
        mJobParameters = job;
        getRemaining(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result) {

            }

            @Override
            public void onSuccessJsonObject(JSONObject result) {
                /*try {
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
                }*/
            }

            @Override
            public void onSuccessJsonArray(JSONArray result) {
                try {
                    ArrayList<String> IDs = new ArrayList<String>();
                    ArrayList<String> Indexs = new ArrayList<String>();
                    cdtsJSON = new JSONArray();
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject res = result.getJSONObject(i);
                        Log.d("JobService", res.getString("tgl_selesai").substring(0, 10) + "T" + res.getString("tgl_selesai").substring(11, res.getString("tgl_selesai").length() - 7) + "Z");
                        Date resultDate = formatOld.parse(res.getString("tgl_selesai").substring(0, 10) + "T" + res.getString("tgl_selesai").substring(11, res.getString("tgl_selesai").length() - 7) + "Z");
                        long seconds = (resultDate.getTime() - currentTime.getTime());
                        int dateInSeconds = Integer.parseInt(String.valueOf(seconds));
                        IDs.add(res.getString("device_id"));
                        Indexs.add(res.getString("id_jemuran"));
                        cdts.add(i, new HistoryTimer(dateInSeconds, 1000, res.getString("device_id"), res.getString("id_jemuran")).start());
                        JSONObject cdtJSON = new JSONObject();
                        cdtJSON.put("deviceID", res.getString("device_id"));
                        cdtJSON.put("listIndex", i);
                        cdtsJSON.put(cdtJSON);
                    }
                    broadcastingID(IDs, Indexs);
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return true;
    }

    private void broadcastingID(ArrayList<String> id, ArrayList<String> indexs) {
        Intent intentID = new Intent(COUNTDOWN_BR);
        intentID.putExtra("idArray", id);
        intentID.putExtra("indexArray", indexs);
        sendBroadcast(intentID);
    }

    private void updateStatus(String id) {
        final String idJem = id;
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
                body.put("id", idJem);
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
                    JSONArray res = new JSONArray();
                    List<String> idDevice = new ArrayList<>();
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject history = result.getJSONObject(i);
                        if (history.getString("status").equals("belum kering") && !idDevice.contains(history.getString("device_id"))) {
                            idDevice.add(history.getString("device_id"));
                            res.put(history);
                        }
                    }
                    callback.onSuccessJsonArray(res);
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
        /*if(cdt!=null){
            cdt.cancel();
        }*/
        if (!cdts.isEmpty()) {
            for (int i = 0; i < cdts.size(); i++) {
                CountDownTimer cTimer = cdts.get(i);
                cTimer.cancel();
            }
        }
        getBaseContext().unregisterReceiver(broadcastReceiver);
        Log.d("JobService", "JobStopped");
        return false;
    }

    public class HistoryTimer extends CountDownTimer {
        public String idDevice = "";
        public String idJemuran = "";

        public HistoryTimer(long millisInFuture, long countDownInterval, String idDevice, String idJemuran) {
            super(millisInFuture, countDownInterval);
            this.idDevice = idDevice;
            this.idJemuran = idJemuran;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int hours = (int) TimeUnit.SECONDS.toHours(millisUntilFinished / 1000);
            int minutes = (int) TimeUnit.SECONDS.toMinutes(millisUntilFinished / 1000) - (hours * 60);
            bi.putExtra("id", idDevice);
            bi.putExtra("hours", hours);
            bi.putExtra("minutes", minutes);
            sendBroadcast(bi);
        }

        @Override
        public void onFinish() {
            updateStatus(idJemuran);
            Log.d("JobService " + idDevice, "countdown finish");
            Intent done = new Intent(COUNTDOWN_BR);
            done.putExtra("updateList", true);
            sendBroadcast(done);
        }
    }
}
