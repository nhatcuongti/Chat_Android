package com.example.meza;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.meza.model.User;

import java.net.MalformedURLException;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

// Java
// Java

public class VideoCallScreen extends AppCompatActivity implements View.OnClickListener {

    String curUserID;
    // Java
    // Fill the App ID of your project generated on Agora Console.
    private String appId = "e5cf25f6acaa4808a27459bd1ea82edd";
    // Fill the channel name.
    private String appCertificate = "4db004afc19a439992fd082560165ac8";
    private String channelName = "test";
    // Fill the temp token generated on Agora Console.
//    private String token = "006e5cf25f6acaa4808a27459bd1ea82eddIAC45W74tGr1WyPWTvdS5Fz4nPhTCdFxETErauWzyZmcNAx+f9gAAAAAEABrDG+dUFZJYgEAAQBPVkli";
    private String token;

    String urlStr = "";
    private RtcEngine mRtcEngine;

    // Java
    private static final int PERMISSION_REQ_ID = 22;

    private static final String[] REQUESTED_PERMISSIONS = {
    Manifest.permission.RECORD_AUDIO,
    Manifest.permission.CAMERA
};

    // btn
    ImageView muteBtn, hangonBtn, speakerBtn;
    String urlBase = "https://mezatoken.herokuapp.com/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_screen);

        //inflate btn
        inflateBtn();

        Bundle bundle = getIntent().getExtras();
        token = bundle.getString("token");
        channelName = bundle.getString("channelName");

        User curUser = User.getCurrentUser(getApplicationContext());
        curUserID = curUser.getPhone_number();

        // If all the permissions are granted, initialize the RtcEngine object and join a channel.
//        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
//        checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID)) {
//
//    }
        try {
            initializeAndJoinChannel();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void inflateBtn() {
        hangonBtn = findViewById(R.id.hangon_video_Btn);
        hangonBtn.setOnClickListener(this);
    }



    private void initializeAndJoinChannel() throws MalformedURLException {
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
//        String token1 =  new RtcTokenBuilder().buildTokenWithUserAccount(appId, appCertificate, channelName,curUserID, RtcTokenBuilder.Role.Role_Admin, 1000);
//        token = new MyTask().doInBackground("https://mezatoken.herokuapp.com/", "my-channel", "0");
        Log.d("token", token);
        mRtcEngine.joinChannel(token,"meza" +  channelName, "",  0);
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



//    public  void fetchToken(String urlBase, String channelName, String userId)  {
////        Logger log = LoggerFactory.getLogger("AgoraTokenRequester");
//
//        OkHttpClient client = new OkHttpClient();
//        String url = urlBase + "/rtc/" + channelName + "/publisher/uid/" + userId + "/";
//
//        okhttp3.Request request = new okhttp3.Request.Builder()
//                .url(url)
//                .method("GET", null)
//                .build();
//
//        try (okhttp3.Response response = client.newCall(request).execute()) {
//            if (!response.isSuccessful()) {
//                Log.d("Unexpected code " , response.toString());
//            } else {
//                JSONObject jObject = new JSONObject(response.body().string());
//                return jObject.getString("rtcToken");
//            }
//        } catch (IOException | JSONException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.hangon_video_Btn:
                mRtcEngine.leaveChannel();
                mRtcEngine.destroy();
                finish();
                break;
        }
    }


}


