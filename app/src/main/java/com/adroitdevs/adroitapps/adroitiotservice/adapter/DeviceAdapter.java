package com.adroitdevs.adroitapps.adroitiotservice.adapter;

import android.content.Context;
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

    ArrayList<Device> deviceList = new ArrayList<>();
    IDeviceAdapter mIDeviceAdapter;

    public DeviceAdapter(Context context, ArrayList<Device> deviceList) {
        this.deviceList = deviceList;
        mIDeviceAdapter = (IDeviceAdapter) context;
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

    public interface IDeviceAdapter {
        void doDetails();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDevice;
        TextView linkMore;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDevice = (TextView) itemView.findViewById(R.id.tvDevice);
            linkMore = (TextView) itemView.findViewById(R.id.linkMore);

            linkMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIDeviceAdapter.doDetails();
                }
            });
        }
    }
}
