package com.adroitdevs.adroitapps.adroitiotservice.service;

import android.util.Log;

import com.adroitdevs.adroitapps.adroitiotservice.VolleySingleton;
import com.adroitdevs.adroitapps.adroitiotservice.model.TokenPrefrences;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rexchris on 09/09/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private String token;

    @Override
    public void onTokenRefresh() {
        if (TokenPrefrences.getToken(getBaseContext()) != null) {
            String regToken = FirebaseInstanceId.getInstance().getToken();
            sendToServer(regToken);
        }
    }

    private void sendToServer(String regToken) {
        token = regToken;
        String url = "http://192.168.43.200:3000/profile";
        StringRequest request = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.getBoolean("status")) {
                        Log.d("InstanceID", "Success");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Log.e("InstanceID", new String(error.networkResponse.data, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    Log.e("InstanceID", e.toString());
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> body = new HashMap<>();
                body.put("regToken", token);
                return body;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> head = new HashMap<>();
                head.put("Content-type", "application/json");
                head.put("Authorization", TokenPrefrences.getToken(getBaseContext()));
                return head;
            }
        };
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(request);
    }
}
