package com.adroitdevs.adroitapps.adroitiotservice.adapter;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.adroitdevs.adroitapps.adroitiotservice.R;
import com.adroitdevs.adroitapps.adroitiotservice.VolleyCallback;
import com.adroitdevs.adroitapps.adroitiotservice.model.Device;

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
        holder.cahayaText.setText(device.cahaya + " cd");
        holder.hujanText.setText(device.hujan + " mm");
        holder.statusText.setText(device.servo);
        holder.status.setChecked(device.status.equals("On"));
        if (device.status.equals("On")) {
            holder.servoSwitch.setChecked(device.servo.equals("Jemur"));
            holder.servoSwitch.setEnabled(device.auto.equals("Manual"));
        } else {
            off(holder);
        }

        holder.status.setOnCheckedChangeListener(new StatSwitch(holder, position));
        holder.servoSwitch.setOnCheckedChangeListener(new servoSwitch(holder, position));

    }

    public void on(ViewHolder holder) {
        holder.manual.setEnabled(true);
        holder.auto.setEnabled(true);
        holder.servoSwitch.setEnabled(true);
    }

    public void off(ViewHolder holder) {
        holder.manual.setEnabled(false);
        holder.auto.setEnabled(false);
        holder.servoSwitch.setChecked(false);
        holder.servoSwitch.setEnabled(false);
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
                        if (stat.equals("On")) {
                            on(holder);
                        } else {
                            holder.servoSwitch.setOnCheckedChangeListener(null);
                            off(holder);
                            holder.statusText.setText("angkat");
                            holder.servoSwitch.setOnCheckedChangeListener(new servoSwitch(holder, id));
                        }
                    }
                    holder.status.setOnCheckedChangeListener(new StatSwitch(holder, id));
                }
            });
        }
    }

    class servoSwitch implements CompoundButton.OnCheckedChangeListener {

        ViewHolder holder;
        int id;
        String servo = "";

        public servoSwitch(ViewHolder holder, int id) {
            this.holder = holder;
            this.id = id;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {
            holder.servoSwitch.setOnCheckedChangeListener(null);
            if (b) {
                servo = "jemur";
            } else {
                servo = "angkat";
            }
            mIDeviceAdapter.status(servo, id, new VolleyCallback() {
                @Override
                public void onSuccess(boolean result) {
                    if (!result) {
                        holder.servoSwitch.setChecked(!b);
                    } else {
                        holder.statusText.setText(servo);
                    }
                    holder.servoSwitch.setOnCheckedChangeListener(new servoSwitch(holder, id));
                }
            });
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDevice, cahayaText, hujanText, statusText;
        Switch status, servoSwitch;
        Button manual, auto;

        ViewHolder(View itemView) {
            super(itemView);
            tvDevice = (TextView) itemView.findViewById(R.id.namaDevice);
            cahayaText = (TextView) itemView.findViewById(R.id.Cahaya);
            hujanText = (TextView) itemView.findViewById(R.id.Cuaca);
            statusText = (TextView) itemView.findViewById(R.id.Status);
            status = (Switch) itemView.findViewById(R.id.switchDevice);
            servoSwitch = (Switch) itemView.findViewById(R.id.switchJemur);
        }
    }
}
