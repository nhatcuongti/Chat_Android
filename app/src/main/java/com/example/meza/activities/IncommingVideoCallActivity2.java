package com.example.meza.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.fragment.app.FragmentActivity;

import com.example.meza.R;
import com.stringee.call.StringeeCall;
import com.stringee.common.StringeeAudioManager;

import org.json.JSONObject;

import java.util.Set;

/**
 * Created by reiko-lhnhat on 4/11/2022.
 */
public class IncommingVideoCallActivity2 extends FragmentActivity {
    private StringeeCall stringeeCall;
    private StringeeAudioManager audioManager;

    FrameLayout mLocalViewContainer;
    FrameLayout mRemoteViewContainer;

    Boolean isMute = false, isExternalSpeaker = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_screen);

        mLocalViewContainer = (FrameLayout) findViewById(R.id.local_video_view_container);
        mRemoteViewContainer = (FrameLayout) findViewById(R.id.remote_video_view_container);

        // lay du lieu tu intent
        Intent intent = getIntent();
        String callID = intent.getStringExtra("call_id");


        stringeeCall = (StringeeCall) CallsMap.getData(callID);


        audioManager = StringeeAudioManager.create(this);
        audioManager.start(new StringeeAudioManager.AudioManagerEvents() {
            @Override
            public void onAudioDeviceChanged(StringeeAudioManager.AudioDevice audioDevice, Set<StringeeAudioManager.AudioDevice> set) {

            }
        });

        audioManager.setSpeakerphoneOn(true);


        stringeeCall.setCallListener(new StringeeCall.StringeeCallListener() {
            @Override
            public void onSignalingStateChange(StringeeCall stringeeCall, StringeeCall.SignalingState signalingState, String s, int i, String s1) {
                switch (signalingState) {
                    case CALLING:
                        Log.d("call123", "call: " + "calling ");
                        break;
                    case RINGING:
//                        state.setText("Đang đổ chuông");
                        Log.d("call123", "call: " + "ringing");

                        break;
                    case ANSWERED:
                        Log.d("call123", "call: " + "anser");
                        break;
                    case BUSY:
                        Log.d("call123", "call: " + "busy");
                        audioManager.stop();
                        finish();
                        break;
                    case ENDED:
                        finish();
                        Log.d("call123", "call: " + "ended");
                        break;
                }
            }

            @Override
            public void onError(StringeeCall stringeeCall, int i, String s) {
                Log.d("call123", "call: " + "onError");

            }

            @Override
            public void onHandledOnAnotherDevice(StringeeCall stringeeCall, StringeeCall.SignalingState signalingState, String s) {

            }

            @Override
            public void onMediaStateChange(StringeeCall stringeeCall, StringeeCall.MediaState mediaState) {
                Log.d("call123", "call: " + "onMediaStateChange" + mediaState.toString());
                switch (mediaState.toString()){
                    case "DISCONNECTED":
                        finish();
                        break;
                }
            }

            @Override
            public void onLocalStream(StringeeCall stringeeCall) {
                Log.d("call123", "call: " + "onLocalStream");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (stringeeCall.isVideoCall()) {
                            mLocalViewContainer.addView(stringeeCall.getLocalView());
                            stringeeCall.renderLocalView(true);
                        }
                    }
                });

            }

            @Override
            public void onRemoteStream(StringeeCall stringeeCall) {
                Log.d("call123", "call: " + "onRemoteStream");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (stringeeCall.isVideoCall()) {
                            mRemoteViewContainer.addView(stringeeCall.getRemoteView());
                            stringeeCall.renderRemoteView(false);
                        }
                    }
                });
            }

            @Override
            public void onCallInfo(StringeeCall stringeeCall, JSONObject jsonObject) {

            }
        });

        stringeeCall.answer();


    }


    @Override
    protected void onDestroy() {


        super.onDestroy();
        Log.d("call", "call: " + "onDetroy");
//        Utils.countInCommingCallAtMoment = 0;
        audioManager.stop();

    }
}
