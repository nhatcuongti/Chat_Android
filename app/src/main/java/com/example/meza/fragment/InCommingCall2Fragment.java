package com.example.meza.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.meza.R;
import com.example.meza.activities.IncommingCallActivity;


public class InCommingCall2Fragment extends Fragment {
    IncommingCallActivity main;
    ImageView hangout, mute, speaker;
    Boolean isMute = false, isExternalSpeaker = false;

    public InCommingCall2Fragment(IncommingCallActivity main) {
        this.main = main;
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_in_comming_call2, container, false);
        hangout = view.findViewById(R.id.hangon_Btn_in_frag_2);
        mute = view.findViewById(R.id.mute_fag2_btn);
        speaker = view.findViewById(R.id.speaker_fag2_btn);

        hangout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.fragmentToActivity("hangout");
            }
        });

        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.fragmentToActivity("mute");
                if(isMute){
                    isMute = false;
                    mute.setImageResource(R.drawable.btn_mute);
                }
                else {
                    isMute = true;
                    mute.setImageResource(R.drawable.btn_mute_enable);
                }
            }
        });

        speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.fragmentToActivity("speaker");
                if(isExternalSpeaker){
                    isExternalSpeaker = false;
                    speaker.setImageResource(R.drawable.btn_speaker);
                }
                else {
                    isExternalSpeaker = true;
                    speaker.setImageResource(R.drawable.btn_speaker_enable);
                }
            }
        });



        return view;
    }
}