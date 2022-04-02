package com.example.meza;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
// Java
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

// Java
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.meza.model.User;
import com.example.meza.utilities.RtcTokenBuilder;
import com.example.meza.utilities.Utilities;

public class VideoCallScreen extends AppCompatActivity implements View.OnClickListener {

    String curUserID;
    // Java
    // Fill the App ID of your project generated on Agora Console.
    private String appId = "e5cf25f6acaa4808a27459bd1ea82edd";
    // Fill the channel name.
    private String appCertificate = "4db004afc19a439992fd082560165ac8";
    private String channelName = "test";
    // Fill the temp token generated on Agora Console.
    private String token = "006e5cf25f6acaa4808a27459bd1ea82eddIAC45W74tGr1WyPWTvdS5Fz4nPhTCdFxETErauWzyZmcNAx+f9gAAAAAEABrDG+dUFZJYgEAAQBPVkli";
    private RtcEngine mRtcEngine;

    // Java
    private static final int PERMISSION_REQ_ID = 22;

    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    // btn
    ImageView muteBtn, hangonBtn, speakerBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_screen);

        //inflate btn
        inflateBtn();

        User curUser = User.getCurrentUser(getApplicationContext());
        curUserID = curUser.getPhone_number();
        // If all the permissions are granted, initialize the RtcEngine object and join a channel.
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID)) {
            initializeAndJoinChannel();
        }
    }

    private void inflateBtn() {
        hangonBtn = findViewById(R.id.hangon_video_Btn);
        hangonBtn.setOnClickListener(this);
    }



    private void initializeAndJoinChannel() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), appId, mRtcEventHandler);
        } catch (Exception e) {
            throw new RuntimeException("Check the error.");
        }

        // By default, video is disabled, and you need to call enableVideo to start a video stream.
        mRtcEngine.enableVideo();

        FrameLayout container = findViewById(R.id.local_video_view_container);
        // Call CreateRendererView to create a SurfaceView object and add it as a child to the FrameLayout.
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        container.addView(surfaceView);
        // Pass the SurfaceView object to Agora so that it renders the local video.
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0));

        // Join the channel with a token.
//        token =  new RtcTokenBuilder().buildTokenWithUserAccount(appId, appCertificate, channelName,curUserID, RtcTokenBuilder.Role.Role_Admin, 1000);
        mRtcEngine.joinChannel(token, channelName, "", 0);
    }

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        // Listen for the remote user joining the channel to get the uid of the user.
        public void onUserJoined(int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Call setupRemoteVideo to set the remote video view after getting uid from the onUserJoined callback.
                    setupRemoteVideo(uid);
                }
            });
        }
    };

    private void setupRemoteVideo(int uid) {
        FrameLayout container = findViewById(R.id.remote_video_view_container);
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.hangon_video_Btn:
                mRtcEngine.leaveChannel();
                mRtcEngine.destroy();
                break;
        }
    }
}