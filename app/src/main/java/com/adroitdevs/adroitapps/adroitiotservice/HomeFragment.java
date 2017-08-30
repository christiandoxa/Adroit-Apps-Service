package com.adroitdevs.adroitapps.adroitiotservice;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adroitdevs.adroitapps.adroitiotservice.adapter.DeviceAdapter;
import com.adroitdevs.adroitapps.adroitiotservice.model.Device;
import com.adroitdevs.adroitapps.adroitiotservice.model.TokenPrefrences;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements DeviceAdapter.IDeviceAdapter {
    IListener mListener;
    DeviceAdapter mAdapter;
    ArrayList<Device> mList = new ArrayList<>();
    // TODO: Rename and change types of parameters
    ProgressDialog progressDialog;
    Context context;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getContext();
        mListener = (IListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        RecyclerView rv = (RecyclerView) view.findViewById(R.id.frame);
        LinearLayoutManager lm = new LinearLayoutManager(this.getContext());
        rv.setLayoutManager(lm);
        mAdapter = new DeviceAdapter(this, mList);
        fillData();
        rv.setAdapter(mAdapter);
        return view;
    }

    private void fillData() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Mengambil data");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        String url = "http://angkatin.arkademy.com:3000/profile";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("status")) {
                        JSONObject result = response.getJSONObject("result");
                        JSONObject profile = result.getJSONObject("profile");
                        mListener.setTextProfile(profile.getString("nama"), profile.getString("email"));
                        JSONArray devices = result.getJSONArray("devices");
                        for (int i = 0; i < devices.length(); i++) {
                            JSONObject device = devices.getJSONObject(i);
                            Gson gson = new Gson();
                            mList.add(gson.fromJson(device.toString(), Device.class));
                        }
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Get data error", Toast.LENGTH_LONG).show();
                    }
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Error : " + e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Status", error.toString());
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Connection Error : " + error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> head = new HashMap<>();
                head.put("Conten-Type", "application/json");
                head.put("Authorization", "Bearer " + TokenPrefrences.getToken(context));
                return head;
            }
        };
        VolleySingleton.getInstance(this.getContext()).addToRequestQueue(request);
    }

    @Override
    public void status(final String stat, final int id, final VolleyCallback callback) {
        String url = "http://angkatin.arkademy.com:3000/update";
        final Device device = mList.get(id);
        progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("ReqStat", response + " " + stat);
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.getBoolean("status")) {
                        callback.onSuccess(true);
                    } else {
                        callback.onSuccess(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onSuccess(false);
                progressDialog.dismiss();
                Log.d("ReqStat", error.toString() + " " + stat);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> body = new HashMap<>();
                body.put("id", device.device_id);
                body.put("job", stat);
                return body;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> head = new HashMap<>();
                head.put("Conten-Type", "application/json");
                head.put("Authorization", "Bearer " + TokenPrefrences.getToken(context));
                return head;
            }
        };
        VolleySingleton.getInstance(this.getContext()).addToRequestQueue(request);
    }

    interface IListener {
        void setTextProfile(String nama, String email);
    }
}
