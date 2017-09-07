package com.adroitdevs.adroitapps.adroitiotservice.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adroitdevs.adroitapps.adroitiotservice.R;
import com.adroitdevs.adroitapps.adroitiotservice.model.RiwayatJemur;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by rexchris on 05/09/17.
 */

public class RiwayatAdapter extends RecyclerView.Adapter<RiwayatAdapter.ViewHolder> {
    ArrayList<RiwayatJemur> listRiwayat = new ArrayList<>();
    RiwayatJemur riwayatJemur;
    Fragment contextFrag;

    public RiwayatAdapter(Fragment contextFrag, ArrayList<RiwayatJemur> listRiwayat) {
        this.listRiwayat = listRiwayat;
        this.contextFrag = contextFrag;
    }

    @Override
    public RiwayatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.jemuran_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        riwayatJemur = listRiwayat.get(position);
        int time = Integer.parseInt(String.valueOf(TimeUnit.SECONDS.toMinutes(Integer.parseInt(riwayatJemur.estimasi_waktu))));
        holder.waktu.setText(String.valueOf(time) + " menit");
        holder.stat.setText(riwayatJemur.status);
        holder.tgl.setText(riwayatJemur.tanggal_jemur);
        if (!riwayatJemur.status.equals("belum kering")) {
            holder.side.setBackgroundColor(ContextCompat.getColor(contextFrag.getContext(), R.color.colorPrimary));
            holder.circ.setBackgroundResource(R.drawable.gree_circle);
            holder.stat.setTextColor(ContextCompat.getColor(contextFrag.getContext(), R.color.colorPrimary));
        }
    }

    @Override
    public int getItemCount() {
        if (listRiwayat != null) {
            return listRiwayat.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tgl, stat, waktu;
        View side, circ;

        public ViewHolder(View itemView) {
            super(itemView);
            tgl = (TextView) itemView.findViewById(R.id.riwTgl);
            stat = (TextView) itemView.findViewById(R.id.riwStatus);
            waktu = (TextView) itemView.findViewById(R.id.riwTime);
            side = itemView.findViewById(R.id.sideView);
            circ = itemView.findViewById(R.id.bunderan);
        }
    }
}
