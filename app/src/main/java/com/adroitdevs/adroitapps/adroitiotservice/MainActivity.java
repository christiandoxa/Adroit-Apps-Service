package com.adroitdevs.adroitapps.adroitiotservice;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.adroitdevs.adroitapps.adroitiotservice.adapter.DeviceAdapter;
import com.adroitdevs.adroitapps.adroitiotservice.model.Device;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<Device> mList = new ArrayList<>();
    DeviceAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView rv = (RecyclerView) findViewById(R.id.listDevice);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        rv.setLayoutManager(lm);
        mAdapter = new DeviceAdapter(mList);
        rv.setAdapter(mAdapter);
        fillData();
    }

    private void fillData() {
        Resources rs = getResources();
        String[] arDevice = rs.getStringArray(R.array.places);
        String[] arDeskripsi = rs.getStringArray(R.array.place_desc);
        for (int i = 0; i < arDevice.length; i++) {
            mList.add(new Device(arDevice[i], arDeskripsi[i]));
        }
        mAdapter.notifyDataSetChanged();
    }
}
