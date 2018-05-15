package com.adroitdevs.adroitapps.adroitiotservice;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

public class StatusFragment extends Fragment implements DeviceAdapter.IDeviceAdapter {
    IListener mListener;
    DeviceAdapter mAdapter;
    ArrayList<Device> mList = new ArrayList<>();
    // TODO: Rename and change types of parameters
    ProgressDialog progressDialog;
    Context context;

    public StatusFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListener = (IListener) this.getContext();
        context = this.getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_status, container, false);
        RecyclerView rv = view.findViewById(R.id.listDevice);
        LinearLayoutManager lm = new LinearLayoutManager(this.getContext());
        rv.setLayoutManager(lm);
        mAdapter = new DeviceAdapter(this, mList);
        rv.setAdapter(mAdapter);
        fillData();
        return view;

    }

    private void fillData() {
        progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setMessage("Mengambil data");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        String url = "http://192.168.43.200:3000/UserAPI/profile";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject profile = response.getJSONObject("profile");
                    mListener.setTextProfile(profile.getString("namaUser"), profile.getString("emailUser"));
                    JSONArray devices = response.getJSONArray("device");
                    for (int i = 0; i < devices.length(); i++) {
                        JSONObject device = devices.getJSONObject(i);
                        Gson gson = new Gson();
                        mList.add(gson.fromJson(device.toString(), Device.class));
                    }
                    mAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Status", error.toString());
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Connection Error", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> head = new HashMap<>();
                head.put("Conten-Type", "application/json");
                head.put("KEY", TokenPrefrences.getToken(context));
                return head;
            }
        };
        VolleySingleton.getInstance(this.getContext()).addToRequestQueue(request);
    }

    @Override
    public void status(final String stat, final int id, final VolleyCallback callback) {
        String url = "http://192.168.43.200:3000/UserAPI";
        final Device device = mList.get(id);
        progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.d("ReqStat", response + " " + stat);
                callback.onSuccess(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.d("ReqStat", error.toString() + " " + stat);
                callback.onSuccess(false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> body = new HashMap<>();
                body.put("id", device.device_id);
                body.put("job", stat);
                return body;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> head = new HashMap<>();
                head.put("Conten-Type", "application/json");
                head.put("KEY", TokenPrefrences.getToken(context));
                return head;
            }
        };
        VolleySingleton.getInstance(this.getContext()).addToRequestQueue(request);
    }

    interface IListener {
        void setTextProfile(String nama, String email);
    }


}
