package com.adroitdevs.adroitapps.adroitiotservice;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adroitdevs.adroitapps.adroitiotservice.adapter.DeviceAdapter;
import com.adroitdevs.adroitapps.adroitiotservice.adapter.RiwayatAdapter;
import com.adroitdevs.adroitapps.adroitiotservice.model.Device;
import com.adroitdevs.adroitapps.adroitiotservice.model.RiwayatJemur;
import com.adroitdevs.adroitapps.adroitiotservice.model.TokenPrefrences;
import com.adroitdevs.adroitapps.adroitiotservice.service.MyJobService;
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
    private static final String URL = "http://192.168.43.200:3000/";
    private static final String SAVED_ID = "savedIdArray";
    private static final String SAVED_ID_COUNTDOWN = "savedIdCountdown";
    private static final String SAVED_INDEX = "savedIndexArray";
    IListener mListener;
    DeviceAdapter mAdapter;
    RiwayatAdapter riwayatAdapter;
    ArrayList<Device> mList = new ArrayList<>();
    String idCountdown = "";
    ArrayList<RiwayatJemur> listJemur = new ArrayList<>();
    // TODO: Rename and change types of parameters
    ProgressDialog progressDialog;
    Context context;
    TextView listTitle, jemuranList, deviceList, hour, minute;
    Spinner listCountdown;
    RecyclerView rv;
    Gson gson = new Gson();
    ArrayList<String> idArray = new ArrayList<>();
    ArrayList<String> indexArray = new ArrayList<>();
    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                if (intent.hasExtra("updateList")) {
                    updateListRiwayat();
                    Log.d("HomeFragment", "updateList");
                }
                if (intent.hasExtra("idArray") && intent.hasExtra("indexArray")) {
                    idArray = intent.getStringArrayListExtra("idArray");
                    indexArray = intent.getStringArrayListExtra("indexArray");
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, idArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Log.d("HomeFragment", "idArray : " + idArray.toString());
                    Log.d("HomeFragment", "indeArray : " + indexArray.toString());
                    listCountdown.setAdapter(adapter);
                    if (!idArray.isEmpty()) {
                        idCountdown = idArray.get(0);
                    } else {
                        idCountdown = "";
                        hour.setText("0");
                        minute.setText("0");
                    }
                }
            }
            updateTimer(intent);
        }
    };

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("HomeFragment", "onCreateView");
        context = this.getContext();
        mListener = (IListener) context;
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        FloatingActionButton fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        listTitle = view.findViewById(R.id.TextSub);
        jemuranList = view.findViewById(R.id.listRiwayat);
        deviceList = view.findViewById(R.id.listDevices);
        hour = view.findViewById(R.id.hourTime);
        minute = view.findViewById(R.id.minuteTime);
        listCountdown = view.findViewById(R.id.deviceSpinner);
        listCountdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                idCountdown = listCountdown.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        LinearLayoutManager lm = new LinearLayoutManager(this.getContext());
        rv = view.findViewById(R.id.frame);
        rv.setLayoutManager(lm);
        fillData();
        mAdapter = new DeviceAdapter(this, mList);
        riwayatAdapter = new RiwayatAdapter(this, listJemur);
        rv.setAdapter(mAdapter);
        listTitle.setText("Devices");
        jemuranList.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBackroundWhite));
        jemuranList.setBackgroundResource(R.drawable.roun_rect_gray);
        deviceList.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray));
        deviceList.setBackgroundResource(R.drawable.roun_rect_white);
        jemuranList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rv.setAdapter(null);
                rv.setAdapter(riwayatAdapter);
                jemuranList.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray));
                jemuranList.setBackgroundResource(R.drawable.roun_rect_white);
                deviceList.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBackroundWhite));
                deviceList.setBackgroundResource(R.drawable.roun_rect_gray);
                listTitle.setText("Riwayat Jemur");
            }
        });

        deviceList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rv.setAdapter(null);
                rv.setAdapter(mAdapter);
                jemuranList.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBackroundWhite));
                jemuranList.setBackgroundResource(R.drawable.roun_rect_gray);
                deviceList.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray));
                deviceList.setBackgroundResource(R.drawable.roun_rect_white);
                listTitle.setText("Devices");
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        Log.d("HomeFragment", "Resume");
        super.onResume();
        this.getActivity().registerReceiver(br, new IntentFilter(MyJobService.COUNTDOWN_BR));
        Intent intentResume = new Intent(MyJobService.COUNTDOWN_BR);
        intentResume.putExtra("resume", true);
        this.getActivity().sendBroadcast(intentResume);
    }

    @Override
    public void onPause() {
        this.getActivity().unregisterReceiver(br);
        Log.d("HomeFragment", "Pause");
        super.onPause();
    }

    @Override
    public void onStop() {
        try {
            this.getActivity().unregisterReceiver(br);
            Log.d("HomeFragment", "Stop");
        } catch (Exception ex) {

        }
        super.onStop();
    }

    public void updateTimer(Intent intent) {
        if (intent.getExtras() != null && intent.hasExtra("hours") && intent.hasExtra("minutes") && intent.hasExtra("id") && intent.getStringExtra("id").equals(idCountdown)) {
            int hours = intent.getIntExtra("hours", 0);
            int minutes = intent.getIntExtra("minutes", 0);
            hour.setText(String.valueOf(hours));
            minute.setText(String.valueOf(minutes));
        }
    }

    private void fillData() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Mengambil data");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        String url = URL + "profile";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("status")) {
                        JSONObject result = response.getJSONObject("result");
                        JSONObject profile = result.getJSONObject("profile");
                        mListener.setTextProfile(profile.getString("nama"), profile.getString("email"));
                        if (!result.isNull("devices")) {
                            JSONArray devices = result.getJSONArray("devices");
                            for (int i = 0; i < devices.length(); i++) {
                                JSONObject device = devices.getJSONObject(i);
                                mList.add(gson.fromJson(device.toString(), Device.class));
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                        if (!result.isNull("riwayat")) {
                            JSONArray riwayats = result.getJSONArray("riwayat");
                            for (int j = 0; j < riwayats.length(); j++) {
                                JSONObject riwayat = riwayats.getJSONObject(j);
                                listJemur.add(gson.fromJson(riwayat.toString(), RiwayatJemur.class));
                            }
                            riwayatAdapter.notifyDataSetChanged();
                        }
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
                if (!error.toString().equals("com.android.volley.TimeoutError")) {
                    if (error.networkResponse.statusCode == 401) {
                        TokenPrefrences.clearToken(getContext());
                        Intent intent = new Intent(getActivity(), SigninActivity.class);
                        progressDialog.dismiss();
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Connection Error : " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Connection Error : " + error.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> head = new HashMap<>();
                head.put("Content-Type", "application/json");
                head.put("Authorization", "Bearer " + TokenPrefrences.getToken(context));
                return head;
            }
        };
        VolleySingleton.getInstance(this.getContext()).addToRequestQueue(request);
    }

    private void updateStatus(String id, final VolleyCallback callback) {
        String url = URL + "history";
        final String idJemuran = id;
        StringRequest request = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    Log.d("update", String.valueOf(res.getBoolean("status")));
                    callback.onSuccess(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("update", error.toString());
                callback.onSuccess(false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> body = new HashMap<>();
                body.put("id", idJemuran);
                return body;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> head = new HashMap<>();
                head.put("Authorization", "Bearer " + TokenPrefrences.getToken(getContext()));
                return head;
            }
        };
        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    @Override
    public void status(final String stat, final int id, final VolleyCallback callback) {
        String url = URL + "update";
        final Device device = mList.get(id);
        progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("ReqStat", stat);
                if (stat.equals("angkat") || stat.equals("Manual") || stat.equals("Off")) {
                    hour.setText("0");
                    minute.setText("0");
                    mListener.stopJob(device.device_id, new VolleyCallback() {
                        @Override
                        public void onSuccess(boolean result) {
                            if (result) {
                                String idUpdate = "";
                                if (idArray.contains(device.device_id)) {
                                    idUpdate = indexArray.get(idArray.indexOf(device.device_id));
                                }
                                updateStatus(idUpdate, new VolleyCallback() {
                                    @Override
                                    public void onSuccess(boolean result) {
                                        if (result) {
                                            updateListRiwayat();
                                        }
                                    }

                                    @Override
                                    public void onSuccessJsonObject(JSONObject result) {

                                    }

                                    @Override
                                    public void onSuccessJsonArray(JSONArray result) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onSuccessJsonObject(JSONObject result) {

                        }

                        @Override
                        public void onSuccessJsonArray(JSONArray result) {

                        }
                    });
                } else if (!stat.equals("On")) {
                    mListener.startJob(device.device_id, new VolleyCallback() {
                        @Override
                        public void onSuccess(boolean result) {
                            if (result) {
                                updateListRiwayat();
                            }
                        }

                        @Override
                        public void onSuccessJsonObject(JSONObject result) {

                        }

                        @Override
                        public void onSuccessJsonArray(JSONArray result) {

                        }
                    });
                }
                progressDialog.dismiss();
                callback.onSuccess(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onSuccess(false);
                progressDialog.dismiss();
                Log.d("ReqStat", error.getMessage() + " " + stat);
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
                head.put("Authorization", "Bearer " + TokenPrefrences.getToken(context));
                return head;
            }
        };
        VolleySingleton.getInstance(this.getContext()).addToRequestQueue(request);
    }

    private void updateListRiwayat() {
        String url = URL + "history";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("status")) {
                        JSONArray result = response.getJSONArray("result");
                        listJemur = new ArrayList<>();
                        int batas = result.length() > 7 ? result.length() - 7 : 0;
                        for (int j = result.length() - 1; j >= batas; j--) {
                            JSONObject riwayat = result.getJSONObject(j);
                            listJemur.add(gson.fromJson(riwayat.toString(), RiwayatJemur.class));
                        }
                        riwayatAdapter.refreshList(listJemur);
                    }
                } catch (JSONException e) {
                    Log.e("HomeFragment", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> head = new HashMap<>();
                head.put("Conten-Type", "application/json");
                head.put("Authorization", "Bearer " + TokenPrefrences.getToken(context));
                return head;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    interface IListener {
        void setTextProfile(String nama, String email);

        void stopJob(String id, VolleyCallback callback);

        void startJob(String id, VolleyCallback callback);
    }
}
