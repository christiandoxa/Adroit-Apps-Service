package com.adroitdevs.adroitapps.adroitiotservice.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rexchris on 05/09/17.
 */

public class RiwayatJemur implements Parcelable {
    public static final Creator<RiwayatJemur> CREATOR = new Creator<RiwayatJemur>() {
        @Override
        public RiwayatJemur createFromParcel(Parcel in) {
            return new RiwayatJemur(in);
        }

        @Override
        public RiwayatJemur[] newArray(int size) {
            return new RiwayatJemur[size];
        }
    };
    public String id_jemuran;
    public String device_id;
    public String tanggal_jemur;
    public String estimasi_waktu;
    public String email;
    public String status;

    protected RiwayatJemur(Parcel in) {
        id_jemuran = in.readString();
        device_id = in.readString();
        tanggal_jemur = in.readString();
        estimasi_waktu = in.readString();
        email = in.readString();
        status = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id_jemuran);
        parcel.writeString(device_id);
        parcel.writeString(tanggal_jemur);
        parcel.writeString(estimasi_waktu);
        parcel.writeString(email);
        parcel.writeString(status);
    }
}
