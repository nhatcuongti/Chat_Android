package com.example.meza.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meza.R;
import com.example.meza.databinding.ActivityFullImageScreenBinding;
import com.example.meza.model.User;
import com.example.meza.utilities.Constants;
import com.example.meza.utilities.JWT;
import com.example.meza.utils.Utils;
import com.google.api.JwtLocation;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import org.w3c.dom.Text;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by reiko-lhnhat on 3/26/2022.
 */
public class VoiceCallingActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView muteBtn, endBtn, speakerBtn;
    String Tag = "statecall";
    CircleImageView avatar;
    TextView name, state;
    Context context = this;
    SinchClient sinchClient;
    User currentUser;
    String userID;

    Call call;

    public VoiceCallingActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_calling);

        endBtn = findViewById(R.id.hangon_Btn);
        endBtn.setOnClickListener(this);

        state = findViewById(R.id.state_call);

        currentUser = User.getCurrentUser(context);
        userID = currentUser.getPhone_number();

        sinchClient = Utils.sinchClient;
        call();
    }

    void call(){
        String receiveUserID = "0987783897";
        if(sinchClient.isStarted()) {
            // contains info of call such as time,participants ,error...
            call = Utils.serviceBinder.callUser(receiveUserID);

            // outgoing call
            call.addCallListener(new CallListener() {
                @Override
                public void onCallProgressing(Call call) {
                    // duoc goi khi cuoc goi dang thuc hien
                    Log.d(Tag, "onCallProgressing");//                    Toast.makeText(getApplicationContext(), "Đang đổ chuông", Toast.LENGTH_LONG);
//                    state.setText("Đang đổ chuông");
                    // them nhac cho hoac hien thi text
                }

                @Override
                public void onCallEstablished(Call call) {
                    // dc goi khi nguoi dung bat may
//                    state.setText("Trong cuộc gọi");
                    // dung nhac cho, thay doi trang thai text
                }

                @Override
                public void onCallEnded(Call call) {
                    // quay ve activity ban dau
                    Log.d(Tag , "cuocgoi ket thuc");
//                    finish();
                }
            });
        }
        else {
            Log.d("err", "SinchClient not started");

            Toast.makeText(getApplicationContext(), "SinchClient not started", Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.hangon_Btn:
                call.hangup();
        }
    }


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
