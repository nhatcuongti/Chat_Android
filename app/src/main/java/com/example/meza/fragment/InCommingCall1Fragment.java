package com.example.meza.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.meza.R;
import com.example.meza.activities.IncommingCallActivity;


public class InCommingCall1Fragment extends Fragment {
    ImageView acceptBtn, declineBtn;
    IncommingCallActivity main;

    public InCommingCall1Fragment(IncommingCallActivity main) {
        this.main = main;
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_in_comming_call1, container, false);
        acceptBtn = view.findViewById(R.id.accept_call_btn);
        declineBtn = view.findViewById(R.id.decline_call_btn);

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.fragmentToActivity("accept");
            }
        });
        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.fragmentToActivity("decline");
            }
        });
        return view;
    }
}