package com.example.user.isotuapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.user.isotuapp.R;
import com.example.user.isotuapp.View.CompleteProfile;


public class HomeFragment extends Fragment {

    View rootView;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        Button tes = (Button) rootView.findViewById(R.id.tes);
        tes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),CompleteProfile.class);
                startActivity(intent);
            }
        });
        return rootView;
    }
}
