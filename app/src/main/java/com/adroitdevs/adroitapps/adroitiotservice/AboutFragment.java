package com.adroitdevs.adroitapps.adroitiotservice;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {

    TextView url;
    CardView web;

    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        url = (TextView) view.findViewById(R.id.textUrl);
        web = (CardView) view.findViewById(R.id.card_view);
        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWeb();
            }
        });
        return view;
    }

    public void openWeb() {
        String urlText = url.getText().toString();
        Uri webUrl = Uri.parse(urlText);
        Intent intent = new Intent(Intent.ACTION_VIEW, webUrl);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}
