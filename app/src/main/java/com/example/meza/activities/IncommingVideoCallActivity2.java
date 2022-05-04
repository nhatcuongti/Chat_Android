package com.example.meza.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;

import com.example.meza.R;
import com.stringee.call.StringeeCall;
import com.stringee.common.StringeeAudioManager;
import com.stringee.listener.StatusListener;

import org.json.JSONObject;

import java.util.Set;

/**
 * Created by reiko-lhnhat on 4/11/2022.
 */
public class IncommingVideoCallActivity2 extends FragmentActivity implements View.OnClickListener {
    private StringeeCall stringeeCall;
    private StringeeAudioManager audioManager;

    FrameLayout mLocalViewContainer;
    FrameLayout mRemoteViewContainer;

    ImageView muteBtn, endBtn, swapCameraBtn, turnCameraBtn;


    boolean isMute = false, cameraOn = true;
    int cameraId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_screen);

        mLocalViewContainer = (FrameLayout) findViewById(R.id.local_video_view_container);
        mRemoteViewContainer = (FrameLayout) findViewById(R.id.remote_video_view_container);
        endBtn = findViewById(R.id.hangon_video_Btn);
        muteBtn = findViewById(R.id.mute_video_outgoing_btn);
        swapCameraBtn = findViewById(R.id.swap_camera_video_outgoing_btn);
        turnCameraBtn = findViewById(R.id.camera_video_outgoing_btn);

        muteBtn.setOnClickListener(this);
        endBtn.setOnClickListener(this);
        swapCameraBtn.setOnClickListener(this);
        turnCameraBtn.setOnClickListener(this);

        // lay du lieu tu intent
        Intent intent = getIntent();
//        Bundle bundle = intent.getExtras();
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

//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (stringeeCall.isVideoCall()) {
//                                    mLocalViewContainer.removeAllViews();
//                                    mLocalViewContainer.addView(stringeeCall.getLocalView());
//                                    stringeeCall.renderLocalView(true);
//                                }
//                            }
//                        });
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
//                Log.d("call123", "call: " + "onMediaStateChange" + mediaState.toString());
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
                            mLocalViewContainer.removeAllViews();
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
                            mRemoteViewContainer.removeAllViews();
                            mRemoteViewContainer.addView(stringeeCall.getRemoteView());
                            stringeeCall.renderRemoteView(true);
                        }
                    }
                });

            }

            @Override
            public void onCallInfo(StringeeCall stringeeCall, JSONObject jsonObject) {

            }
        });

        stringeeCall.ringing(new StatusListener() {
            @Override
            public void onSuccess() {

            }
        });




    }

    @Override
    protected void onStart() {
        super.onStart();
        stringeeCall.answer();
    }

    @Override
    protected void onDestroy() {


        super.onDestroy();
        Log.d("call", "call: " + "onDetroy");
//        Utils.countInCommingCallAtMoment = 0;
        audioManager.stop();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.hangon_video_Btn:
                endCall();
                break;
            case R.id.mute_video_outgoing_btn:
                if(isMute){
                    isMute = false;
                    stringeeCall.mute(false);
                    muteBtn.setImageResource(R.drawable.btn_mute);
                }
                else{
                    isMute = true;
                    stringeeCall.mute(true);
                    muteBtn.setImageResource(R.drawable.btn_mute_enable);
                }
                break;
            case R.id.camera_video_outgoing_btn:
                if(cameraOn){
                    cameraOn = false;
                    stringeeCall.enableVideo(false);                  //set anh
                    turnCameraBtn.setImageResource(R.drawable.btn_disable_camera);
                }
                else{
                    cameraOn = true;
                    stringeeCall.enableVideo(true);
                    //set anh
                    turnCameraBtn.setImageResource(R.drawable.btn_enable_camera);
                }
                break;
            case R.id.swap_camera_video_outgoing_btn:
                stringeeCall.switchCamera(new StatusListener() {
                    @Override
                    public void onSuccess() {
                        cameraId = cameraId == 0 ? 1 : 0;
                    }
                }, cameraId == 0 ? 1 : 0);
        }
    }

    void endCall(){
        stringeeCall.hangup();
        finish();
    }
}
