package com.adroitdevs.adroitapps.adroitiotservice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.adroitdevs.adroitapps.adroitiotservice.model.TokenPrefrences;
import com.adroitdevs.adroitapps.adroitiotservice.service.MyJobService;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomeFragment.IListener {
    private final String URL = "http://10.100.100.179:3000/";
    TextView namaUser, emailUser;
    ProgressDialog progressDialog;
    FirebaseJobDispatcher dispatcher;
    Job myJob;
    Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        changePage(R.id.nav_camera);
        navigationView.setCheckedItem(R.id.nav_camera);
        namaUser = (TextView) header.findViewById(R.id.namaUser);
        emailUser = (TextView) header.findViewById(R.id.emailUser);

        dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

        myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setRecurring(true)
                .setTag("JobService")
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setTrigger(Trigger.executionWindow(0, 0))
                .setReplaceCurrent(true)
                .build();

        dispatcher.mustSchedule(myJob);
    }


    @Override
    protected void onStop() {
        Log.d("Home", "Stop");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.d("Home", "Pause");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Home", "Destroy");
        dispatcher.cancelAll();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        changePage(id);
        return true;
    }

    private void changePage(int id) {

        if (id == R.id.nav_camera) {
            fragment = new HomeFragment();
            setTitle("Beranda");
        } else if (id == R.id.nav_gallery) {
            fragment = new AboutFragment();
            setTitle("Tentang Adroit Devs");
        } else if (id == R.id.nav_send) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
            logOutWeb(new VolleyCallback() {
                @Override
                public void onSuccess(boolean result) {
                    if (result) {
                        logOut();
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

        if (id != R.id.nav_send) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment).commitNow();

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    private void logOutWeb(final VolleyCallback callback) {
        String url = URL + "logout";
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LogOutWeb", error.toString());
                callback.onSuccess(false);
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

    public void logOut() {
        TokenPrefrences.clearToken(this);
        dispatcher.cancelAll();
        Intent intent = new Intent(this, SigninActivity.class);
        intent.putExtra("logout", true);
        progressDialog.dismiss();
        startActivity(intent);
        finish();
    }

    @Override
    public void setTextProfile(String nama, String email) {
        namaUser.setText(nama);
        emailUser.setText(email);
    }

    @Override
    public void stopJob(String id, VolleyCallback callback) {
        callback.onSuccess(true);
        Intent stopIntent = new Intent(MyJobService.COUNTDOWN_BR);
        stopIntent.putExtra("idCount", id);
        sendBroadcast(stopIntent);
        Log.d("ReqStat", "Canceling Job");
    }

    @Override
    public void startJob(final String id, final VolleyCallback callback) {
        String url = URL + "history";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.getBoolean("status")) {
                        dispatcher.mustSchedule(myJob);
                        callback.onSuccess(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onSuccess(false);
                Log.e("Home", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> body = new HashMap<>();
                body.put("id", id);
                body.put("date", Calendar.getInstance().getTime().toString());
                return body;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> head = new HashMap<>();
                head.put("Authorization", "Bearer " + TokenPrefrences.getToken(getBaseContext()));
                return head;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
        Log.d("ReqStat", "Start Job");
    }
}
