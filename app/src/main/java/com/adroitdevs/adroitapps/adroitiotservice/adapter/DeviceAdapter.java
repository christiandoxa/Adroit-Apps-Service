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

/**
 * Created by dhenarra on 27/07/2017.
 */

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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_list, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
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

    }

    public void on(ViewHolder holder, Device device) {
        holder.manual.setEnabled(true);
        holder.auto.setEnabled(true);
        holder.servoSwitch.setEnabled(device.auto.equals("Manual"));
    }

    public void off(ViewHolder holder) {
        holder.manual.setEnabled(false);
        holder.auto.setEnabled(false);
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

        //        void servo(String servo);
        void autom(String auto, int id, VolleyCallback callback);
    }

    public class StatSwitch implements CompoundButton.OnCheckedChangeListener {

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
                            on(holder, device);
                        } else {
                            off(holder);
                        }
                    }
                    holder.status.setOnCheckedChangeListener(new StatSwitch(holder, id));
                }
            });
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDevice, cahayaText, hujanText, statusText;
        Switch status, servoSwitch;
        Button manual, auto;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDevice = (TextView) itemView.findViewById(R.id.tvDevice);
            cahayaText = (TextView) itemView.findViewById(R.id.tvNilaiCahaya);
            hujanText = (TextView) itemView.findViewById(R.id.tvNilaiHujan);
            statusText = (TextView) itemView.findViewById(R.id.tvStat);
            status = (Switch) itemView.findViewById(R.id.switchStatusDevice);
            servoSwitch = (Switch) itemView.findViewById(R.id.switchAngkat);
            manual = (Button) itemView.findViewById(R.id.bManual);
            auto = (Button) itemView.findViewById(R.id.bOtomatis);

            manual.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mIDeviceAdapter.autom("Manual", getAdapterPosition(), new VolleyCallback() {
                        @Override
                        public void onSuccess(boolean result) {
                            if (result) {
                                servoSwitch.setEnabled(true);
                            }
                        }
                    });
                }
            });
            auto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mIDeviceAdapter.autom("Otomatis", getAdapterPosition(), new VolleyCallback() {
                        @Override
                        public void onSuccess(boolean result) {
                            if (result) {
                                servoSwitch.setEnabled(false);
                            }
                        }
                    });
                }
            });
        }
    }
}
