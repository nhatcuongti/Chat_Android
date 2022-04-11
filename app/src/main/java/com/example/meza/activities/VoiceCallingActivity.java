package com.example.meza.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meza.R;
import com.example.meza.model.User;
import com.example.meza.utils.Utils;
import com.stringee.StringeeClient;
import com.stringee.call.StringeeCall;
import com.stringee.common.StringeeAudioManager;

import org.json.JSONObject;

import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by reiko-lhnhat on 3/26/2022.
 */
public class VoiceCallingActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView muteBtn, endBtn, speakerBtn;
    String Tag = "statecall";
    CircleImageView avatar;
    TextView name, state;
    Context context = this;
    StringeeClient client;
//Call call;
    User currentUser;
    String callerId;
    String calleeId;
    private StringeeCall stringeeCall;
    private StringeeAudioManager audioManager;



    public VoiceCallingActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_calling);

        Intent intent = getIntent();
        calleeId = intent.getStringExtra("calleeId");

        endBtn = findViewById(R.id.hangon_Btn);
        endBtn.setOnClickListener(this);

        state = findViewById(R.id.state_call);

        currentUser = User.getCurrentUser(context);
        callerId = "m" + currentUser.getPhone_number();

        client = Utils.stringeeClient;
        Log.d("call","isCon " + client.isConnected() + " " + client.getUserId());



//        sinchClient = Utilss.sinchClient;

//        call();

        makeCAll();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.hangon_Btn:
//                call.hangup();
//                finish();
                endCall();
                break;
        }
    }

    void endCall(){
        stringeeCall.hangup();
        finish();
    }

    void makeCAll(){

        client = Utils.stringeeClient;
        stringeeCall = new StringeeCall(client, callerId, calleeId);
        // Initialize audio manager to manage the audio routing
        audioManager = StringeeAudioManager.create(this);
        audioManager.start(new StringeeAudioManager.AudioManagerEvents() {
            @Override
            public void onAudioDeviceChanged(StringeeAudioManager.AudioDevice audioDevice, Set<StringeeAudioManager.AudioDevice> set) {

            }
        });
        audioManager.setSpeakerphoneOn(false); // false: Audio Call, true: Video Call
// Make a call
        stringeeCall.setVideoCall(false); // false: Audio Call, true: Video Call
        stringeeCall.makeCall();

        stringeeCall.setCallListener(new StringeeCall.StringeeCallListener() {
            @Override
            public void onSignalingStateChange(StringeeCall stringeeCall, StringeeCall.SignalingState signalingState, String s, int i, String s1) {
                Log.d("call", "call: " + "successfully");
                switch (signalingState) {
                    case CALLING:
                        Log.d("call", "call: " + "calling " + calleeId);
                        break;
                    case RINGING:
                        Log.d("call", "call: " + "ring");
                        break;
                    case ANSWERED:
                        Log.d("call", "call: " + "anser");
                        break;
                    case BUSY:
                        Log.d("call", "call: " + "busy");
                        break;
                    case ENDED:
                        Log.d("call", "call: " + "ended");
                        break;
                }

            }

            @Override
            public void onError(StringeeCall stringeeCall, int i, String s) {
                Log.d("call", "callerr: " + s);
            }

            @Override
            public void onHandledOnAnotherDevice(StringeeCall stringeeCall, StringeeCall.SignalingState signalingState, String s) {

            }

            @Override
            public void onMediaStateChange(StringeeCall stringeeCall, StringeeCall.MediaState mediaState) {

            }

            @Override
            public void onLocalStream(StringeeCall stringeeCall) {

            }

            @Override
            public void onRemoteStream(StringeeCall stringeeCall) {

            }

            @Override
            public void onCallInfo(StringeeCall stringeeCall, JSONObject jsonObject) {

            }
        });
    }
//    void handleOutgoingCallState(){
//        stringeeCall.setCallListener(new StringeeCall.StringeeCallListener() {
//            @Override
//            public void onSignalingStateChange(StringeeCall stringeeCall, StringeeCall.SignalingState signalingState, String s, int i, String s1) {
//                Log.d("callstate", signalingState.toString());
//                if(signalingState.toString().equals("ringing"));{
//                    state.setText("Đang đổ chuông");
//                }
//            }
//
//            @Override
//            public void onError(StringeeCall stringeeCall, int i, String s) {
//                //When the client fails to make a call,
//                Log.d("error make call", "onError: " + s + stringeeCall.getFrom());
//            }
//
//            @Override
//            public void onHandledOnAnotherDevice(StringeeCall stringeeCall, StringeeCall.SignalingState signalingState, String s) {
//                //When the call is handled on another device
//            }
//
//            @Override
//            public void onMediaStateChange(StringeeCall stringeeCall, StringeeCall.MediaState mediaState) {
//                //When the call's media stream is connected or disconnected,
//            }
//
//            @Override
//            public void onLocalStream(StringeeCall stringeeCall) {
//
//            }
//
//            @Override
//            public void onRemoteStream(StringeeCall stringeeCall) {
//
//            }
//
//            @Override
//            public void onCallInfo(StringeeCall stringeeCall, JSONObject jsonObject) {
//
//            }
//        });
//    }


//    void handleIncomingCall(){
//        CallClient callClient = sinchClient.getCallClient();
//        callClient.addCallClientListener(new CallClientListener() {
//            @Override
//            public void onIncomingCall(CallClient callClient, Call call) {
//                // phat nhac chuong
//                // phan hoi cuoc goi toi
//
//                call.addCallListener(new CallListener() {
//                    @Override
//                    public void onCallProgressing(Call call) {
//
//                    }
//
//                    @Override
//                    public void onCallEstablished(Call call) {
//
//                    }
//
//                    @Override
//                    public void onCallEnded(Call call) {
//                        //quay ve activity ban dau
//                    }
//                });
//            }
//        });
//    }

}
