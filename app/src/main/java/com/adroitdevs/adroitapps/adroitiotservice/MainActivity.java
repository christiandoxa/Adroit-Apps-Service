package com.adroitdevs.adroitapps.adroitiotservice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements StatusFragment.IListener {

    TextView tvNama, tvEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("EXIT", false)) {
            finish();
        }
        setContentView(R.layout.activity_main);
        tvNama = (TextView) findViewById(R.id.name);
        tvEmail = (TextView) findViewById(R.id.email);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new StatusFragment()).commitNow();
    }

    @Override
    public void setTextProfile(String nama, String email) {
        tvNama.setText(nama);
        tvEmail.setText(email);
    }
}
