package com.example.meza.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meza.R;
import com.example.meza.model.User;
import com.example.meza.services.SoundService;
import com.example.meza.utils.Utils;
import com.stringee.StringeeClient;
import com.stringee.call.StringeeCall;
import com.stringee.common.StringeeAudioManager;
import com.stringee.listener.StatusListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by reiko-lhnhat on 3/26/2022.
 */
public class VideoCallingActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView muteBtn, endBtn, swapCameraBtn, turnCameraBtn;
    FrameLayout mLocalViewContainer;
    FrameLayout mRemoteViewContainer;

    String Tag = "statecall";
    CircleImageView avatar;
    TextView name, state;
    Context context = this;
    StringeeClient client;
//Call call;
    User currentUser;
    String callerId;
    String calleeId;
    String calleeName;
    Bitmap calleeImage;

    boolean isMute = false, cameraOn = true;
    int cameraId = 1;

    private StringeeCall stringeeCall;
    private StringeeAudioManager audioManager;

    //xu li timer
    long startTime, timeInMilliseconds = 0;
    Handler customHandler = new Handler();

    Intent intentSoundService;



    public VideoCallingActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_screen);

        Intent intent = getIntent();
        Bundle  bundle= intent.getExtras();
        calleeId = bundle.getString("calleeId");
        calleeName = bundle.getString("calleeName");
        byte[] byteArray = bundle.getByteArray("calleeImage");
        calleeImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        //
        mLocalViewContainer = (FrameLayout) findViewById(R.id.local_video_view_container);
        mRemoteViewContainer = (FrameLayout) findViewById(R.id.remote_video_view_container);
        endBtn = findViewById(R.id.hangon_video_Btn);
//        state = findViewById(R.id.state_call);
//        avatar = findViewById(R.id.callee_image_out_going);
//        name = findViewById(R.id.callee_name_out_going);
        muteBtn = findViewById(R.id.mute_video_outgoing_btn);
        swapCameraBtn = findViewById(R.id.swap_camera_video_outgoing_btn);
        turnCameraBtn = findViewById(R.id.camera_video_outgoing_btn);

        //
        muteBtn.setOnClickListener(this);
        endBtn.setOnClickListener(this);
        swapCameraBtn.setOnClickListener(this);
        turnCameraBtn.setOnClickListener(this);

//        avatar.setImageBitmap(calleeImage);
//        name.setText(calleeName);

        currentUser = User.getCurrentUser(context);
        callerId = "m" + currentUser.getPhone_number();

        client = Utils.stringeeClient;
        Log.d("call","isCon " + client.isConnected() + " " + client.getUserId());

        intentSoundService = new Intent(this, SoundService.class);
        intentSoundService.putExtra("type", "outGoingCall");



//        sinchClient = Utilss.sinchClient;

//        call();

        makeCAll();
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
        audioManager.setSpeakerphoneOn(true); // false: Audio Call, true: Video Call
// Make a call
        stringeeCall.setVideoCall(true); // false: Audio Call, true: Video Call
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
                        startService(intentSoundService);
                        break;
                    case ANSWERED:
//                        start(state);
                        stopService(intentSoundService);
                        Log.d("call", "call: " + "anser");
                        break;
                    case BUSY:
                        finish();
                        stopService(intentSoundService);
                        Log.d("call", "call: " + "busy");
                        finish();
                        break;
                    case ENDED:
//                        stop(state);
                        Log.d("call", "call: " + "ended");
                        finish();
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (stringeeCall.isVideoCall()) {
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
    }

    // định dạng thời gian
    public static String getDateFromMillis(long d) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(d);
    }

//    public void start(View v) {
//        startTime = SystemClock.uptimeMillis();
//        customHandler.postDelayed(updateTimerThread, 0);
//    }

//    public void stop(View v) {
//        customHandler.removeCallbacks(updateTimerThread);
//    }

//    private Runnable updateTimerThread = new Runnable() {
//        public void run() {
//            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
//            state.setText(getDateFromMillis(timeInMilliseconds));
//            customHandler.postDelayed(this, 1000);
//        }
//    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intentSoundService);
    }
}
