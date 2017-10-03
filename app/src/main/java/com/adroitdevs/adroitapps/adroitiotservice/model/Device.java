package com.adroitdevs.adroitapps.adroitiotservice.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dhenarra on 27/07/2017.
 */

public class Device implements Parcelable {
    public static final Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };
    public String device_id;
    public String status;
    public int cahaya;
    public int hujan;
    public int lembab;
    public String servo;
    public String auto;

    public Device(String device_id, String status, int cahaya, int hujan, int lembab, String servo, String auto) {
        this.device_id = device_id;
        this.status = status;
        this.cahaya = cahaya;
        this.hujan = hujan;
        this.lembab = lembab;
        this.servo = servo;
        this.auto = auto;
    }

    protected Device(Parcel in) {
        device_id = in.readString();
        status = in.readString();
        cahaya = in.readInt();
        hujan = in.readInt();
        lembab = in.readInt();
        servo = in.readString();
        auto = in.readString();
    }

    public void changeStat(String status) {
        this.status = status;
    }

    public void changeServo(String servo) {
        this.servo = servo;
    }

    public void changeAuto(String auto) {
        this.auto = auto;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(device_id);
        parcel.writeString(status);
        parcel.writeInt(cahaya);
        parcel.writeInt(hujan);
        parcel.writeInt(lembab);
        parcel.writeString(servo);
        parcel.writeString(auto);
    }
}
