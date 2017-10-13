package com.adroitdevs.adroitapps.adroitiotservice.adapter;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.adroitdevs.adroitapps.adroitiotservice.R;
import com.adroitdevs.adroitapps.adroitiotservice.VolleyCallback;
import com.adroitdevs.adroitapps.adroitiotservice.model.Device;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

    ArrayList<Device> deviceList = new ArrayList<>();
    IDeviceAdapter mIDeviceAdapter;
    Device device;

    public DeviceAdapter(Fragment context, ArrayList<Device> deviceList) {
        this.deviceList = deviceList;
        mIDeviceAdapter = (IDeviceAdapter) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        device = deviceList.get(position);
        holder.tvDevice.setText(device.device_id);
        String cahaya = device.cahaya > 200 ? "Terang" : "Cahaya Kurang";
        holder.cahayaText.setText("Cahaya : " + cahaya);
        String hujan = device.hujan > 340 ? "Kering" : "Hujan";
        holder.hujanText.setText("Cuaca : " + hujan);
        holder.lembabText.setText(device.lembab + "%");
        holder.status.setChecked(device.status.equals("On"));

        holder.status.setOnCheckedChangeListener(new StatSwitch(holder, position));
    }
    @Override
    public int getItemCount() {
        if (deviceList != null)
            return deviceList.size();
        return 0;
    }

    public interface IDeviceAdapter {
        void status(String stat, int id, VolleyCallback callback);
    }

    class StatSwitch implements CompoundButton.OnCheckedChangeListener {

        ViewHolder holder;
        int id;
        String stat = "";

        public StatSwitch(ViewHolder holder, int id) {
            this.holder = holder;
            this.id = id;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {
            holder.status.setOnCheckedChangeListener(null);
            if (b) {
                stat = "On";
            } else {
                stat = "Off";
            }
            mIDeviceAdapter.status(stat, id, new VolleyCallback() {
                @Override
                public void onSuccess(boolean result) {
                    if (!result) {
                        holder.status.setChecked(!b);
                    } else {
                        deviceList.get(id).changeStat(stat);
                        notifyDataSetChanged();
                    }
                    holder.status.setOnCheckedChangeListener(new StatSwitch(holder, id));
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


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDevice, cahayaText, hujanText, lembabText;
        Switch status;

        ViewHolder(View itemView) {
            super(itemView);
            tvDevice = (TextView) itemView.findViewById(R.id.namaDevice);
            cahayaText = (TextView) itemView.findViewById(R.id.Cahaya);
            hujanText = (TextView) itemView.findViewById(R.id.Cuaca);
            status = (Switch) itemView.findViewById(R.id.switchDevice);
            lembabText = (TextView) itemView.findViewById(R.id.Kelembaban);
        }
    }
}
