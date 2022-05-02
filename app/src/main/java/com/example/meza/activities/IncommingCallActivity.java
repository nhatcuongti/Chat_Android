package com.example.meza.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.meza.R;
import com.example.meza.fragment.InCommingCall1Fragment;
import com.example.meza.fragment.InCommingCall2Fragment;
import com.example.meza.model.User;
import com.example.meza.services.SoundService2;
import com.example.meza.utils.Utils;
import com.stringee.call.StringeeCall;
import com.stringee.common.StringeeAudioManager;
import com.stringee.exception.StringeeError;
import com.stringee.listener.StatusListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by reiko-lhnhat on 4/11/2022.
 */
public class IncommingCallActivity extends FragmentActivity {
    private StringeeCall stringeeCall;
    private StringeeAudioManager audioManager;
    InCommingCall1Fragment fragment1;
    InCommingCall2Fragment fragment2;
    TextView state, callerName;
    CircleImageView avatar;

    long startTime, timeInMilliseconds = 0;
    Handler customHandler = new Handler();
    Intent intentSoundService;

    Boolean isMute = false;
    Boolean isInternalSpeaker = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);


        state = findViewById(R.id.state_call_incomming_call);
        callerName = findViewById(R.id.caller_name_in_comming);
        avatar = findViewById(R.id.caller_image_in_comming);



        //tao intent startservice
        intentSoundService = new Intent(IncommingCallActivity.this, SoundService2.class);
        startService(intentSoundService);

        // lay du lieu tu intent
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String callID = intent.getStringExtra("call_id");
//        String callerId = bundle.getString("callerId");

//        User caller = HomePageActivity.listObjectUserFriend.get(findFriendById(callerId));
//        String name = caller.fullname;
//        String ava = caller.image;


//        callerName.setText(name);
//        avatar.setImageBitmap(Utils.decodeImage(ava));


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
                Log.d("call", "call: " + "onSuccess ");

            }

            @Override
            public void onError(StringeeError stringeeError) {
                super.onError(stringeeError);
                Log.d("call", "call: " + "onError ");
            }

            @Override
            public void onProgress(int i) {
                super.onProgress(i);
                Log.d("call", "call: " + "onProgress ");
            }
        });
        stringeeCall.setCallListener(new StringeeCall.StringeeCallListener() {
            @Override
            public void onSignalingStateChange(StringeeCall stringeeCall, StringeeCall.SignalingState signalingState, String s, int i, String s1) {
                switch (signalingState) {
                    case CALLING:
                        Log.d("call", "call: " + "calling ");
                        break;
                    case RINGING:
//                        state.setText("Đang đổ chuông");
                        break;
                    case ANSWERED:
                        stopService(intentSoundService);
                        start(state);
                        Log.d("call", "call: " + "anser");
                        break;
                    case BUSY:
                        Log.d("call", "call: " + "busy");
                        audioManager.stop();
                        finish();
                        break;
                    case ENDED:
                        stop(state);
                        finish();
                        Log.d("call", "call: " + "ended");
                        break;
                }
            }

            @Override
            public void onError(StringeeCall stringeeCall, int i, String s) {
                Log.d("call", "call: " + "onError");

            }

            @Override
            public void onHandledOnAnotherDevice(StringeeCall stringeeCall, StringeeCall.SignalingState signalingState, String s) {

            }

            @Override
            public void onMediaStateChange(StringeeCall stringeeCall, StringeeCall.MediaState mediaState) {
                Log.d("call", "call: " + "onMediaStateChange" + mediaState.toString());
                switch (mediaState.toString()){
                    case "DISCONNECTED":
                        finish();
                        break;
                }
            }

            @Override
            public void onLocalStream(StringeeCall stringeeCall) {
                Log.d("call", "call: " + "onLocalStream");
            }

            @Override
            public void onRemoteStream(StringeeCall stringeeCall) {
                Log.d("call", "call: " + "onRemoteStream");

            }

            @Override
            public void onCallInfo(StringeeCall stringeeCall, JSONObject jsonObject) {

            }
        });

//        acceptBtn = findViewById(R.id.accept_call_btn);
//        acceptBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

//        declineBtn = findViewById(R.id.decline_call_btn);
//        declineBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });




        // accept and decline
        fragment1 = new InCommingCall1Fragment(this);
        fragment2 = new InCommingCall2Fragment(this);

        replaceFragment(fragment1);


    }

    void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.btn_container, fragment);
        fragmentTransaction.commit();
        stopService(intentSoundService);

    }

    public void fragmentToActivity(String nameOfBtn){
        switch (nameOfBtn){
            case "accept":
                stringeeCall.answer();
                replaceFragment(fragment2);
                break;
            case "decline":
                stringeeCall.hangup();
                finish();
                break;
            case "hangout":
                stringeeCall.hangup();
                finish();
                break;
            case "mute":
                if(isMute == true){
                    stringeeCall.mute(false);
                    isMute = false;
                }
                else {
                    stringeeCall.mute(true);
                    isMute = true;
                }
                break;
            default: break;
        }
    }

    @Override
    protected void onDestroy() {


        super.onDestroy();
        Log.d("call", "call: " + "onDetroy");
        Utils.countInCommingCallAtMoment = 0;
        audioManager.stop();
        stopService(intentSoundService);

    }
    // định dạng thời gian
    public static String getDateFromMillis(long d) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(d);
    }

    public void start(View v) {
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
    }

    public void stop(View v) {
        customHandler.removeCallbacks(updateTimerThread);
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            state.setText(getDateFromMillis(timeInMilliseconds));
            customHandler.postDelayed(this, 1000);
        }
    };
    int findFriendById(String userID){
        String user = userID.substring(1);
        int i = 0;
        for(User u : HomePageActivity.listObjectUserFriend){
            if(u.getId().equals(user)){
                return i;
            }
            i++;
        }
        return -1;
    }
}
