package com.example.meza.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meza.R;
import com.stringee.call.StringeeCall;
import com.stringee.common.StringeeAudioManager;
import com.stringee.listener.StatusListener;

import java.util.Set;

/**
 * Created by reiko-lhnhat on 4/11/2022.
 */
public class IncommingCallActivity extends AppCompatActivity{
    private StringeeCall stringeeCall;
    private StringeeAudioManager audioManager;
    ImageView acceptBtn, declineBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);

        Intent intent = getIntent();
        String callID = intent.getStringExtra("call_id");
        stringeeCall = (StringeeCall) CallsMap.getData(callID);
        audioManager = StringeeAudioManager.create(this);
        audioManager.start(new StringeeAudioManager.AudioManagerEvents() {
            @Override
            public void onAudioDeviceChanged(StringeeAudioManager.AudioDevice audioDevice, Set<StringeeAudioManager.AudioDevice> set) {

            }
        });

        audioManager.setSpeakerphoneOn(false);

        stringeeCall.ringing(new StatusListener() {
            @Override
            public void onSuccess() {

            }
        });

        acceptBtn = findViewById(R.id.accept_call_btn);
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringeeCall.answer();
                acceptBtn.setClickable(false);
            }
        });

        declineBtn = findViewById(R.id.decline_call_btn);
        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringeeCall.hangup();
                finish();
            }
        });
    }

}
