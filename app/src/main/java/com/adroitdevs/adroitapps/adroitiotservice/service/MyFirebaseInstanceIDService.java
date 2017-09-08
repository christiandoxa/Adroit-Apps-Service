package com.adroitdevs.adroitapps.adroitiotservice.service;

import com.adroitdevs.adroitapps.adroitiotservice.VolleySingleton;
import com.adroitdevs.adroitapps.adroitiotservice.model.TokenPrefrences;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rexchris on 09/09/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String regToken = FirebaseInstanceId.getInstance().getToken();

        sendToServer(regToken);
    }

    private void sendToServer(String regToken) {
        final String token = regToken;
        String url = "http://10.103.121.164:3000/profile";
        StringRequest request = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.getBoolean("status")) {
                        sendToServer(token);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> head = new HashMap<>();
                head.put("Content-type", "application/json");
                head.put("Authorization", TokenPrefrences.getToken(getBaseContext()));
                return head;
            }
        };
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(request);
    }
}
