package com.adroitdevs.adroitapps.adroitiotservice.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adroitdevs.adroitapps.adroitiotservice.R;
import com.adroitdevs.adroitapps.adroitiotservice.model.Device;

import java.util.ArrayList;

/**
 * Created by dhenarra on 27/07/2017.
 */

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

    ArrayList<Device> deviceList;

    public DeviceAdapter(ArrayList<Device> deviceList) {
        this.deviceList = deviceList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_list, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Device device = deviceList.get(position);
        holder.tvDevice.setText(device.namaDevice);
    }

    @Override
    public int getItemCount() {
        if (deviceList != null)
            return deviceList.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDevice;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDevice = (TextView) itemView.findViewById(R.id.tvDevice);
        }
    }
}
