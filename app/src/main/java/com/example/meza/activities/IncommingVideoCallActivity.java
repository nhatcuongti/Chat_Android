package com.example.meza.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.example.meza.R;
import com.example.meza.model.User;
import com.example.meza.services.SoundService2;
import com.example.meza.utils.Utils;
import com.stringee.call.StringeeCall;
import com.stringee.common.StringeeAudioManager;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by reiko-lhnhat on 4/11/2022.
 */
public class IncommingVideoCallActivity extends FragmentActivity implements View.OnClickListener {
    private StringeeCall stringeeCall;
    private StringeeAudioManager audioManager;
    String callID;

    TextView state, callerName;
    CircleImageView avatar;
    ImageView accept, decline;

    Intent intentSoundService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_video_calling);


        state = findViewById(R.id.state_call_in_comming_video_call);
        callerName = findViewById(R.id.callee_name_in_comming_video_call);
        avatar = findViewById(R.id.callee_image_in_comming_video_call);
        accept = findViewById(R.id.accept_incomming_video_call_btn);
        decline = findViewById(R.id.decline_incomming_video_call_btn);
        accept.setOnClickListener(this);
        decline.setOnClickListener(this);


        //tao intent startservice
        intentSoundService = new Intent(IncommingVideoCallActivity.this, SoundService2.class);

        // lay du lieu tu intent
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        callID = bundle.getString("call_id");
        String callerId = bundle.getString("callerId");

        User caller = HomePageActivity.listObjectUserFriend.get(findFriendById(callerId));
        String name = caller.fullname;
        String ava = caller.image;


        callerName.setText(name);
        avatar.setImageBitmap(Utils.decodeImage(ava));


        stringeeCall = (StringeeCall) CallsMap.getData(callID);

    }

    @Override
    protected void onStart() {
        super.onStart();
        startService(intentSoundService);
    }

    @Override
    protected void onDestroy() {


        super.onDestroy();
        Log.d("call", "call: " + "onDetroy");
//        Utils.countInCommingCallAtMoment = 0;
//        audioManager.stop();
        stopService(intentSoundService);

    }




    int findFriendById(String userID){
        String user = userID.substring(1);
        int i = 0;
        for(User u : HomePageActivity.listObjectUserFriend){
            if(u.getPhone_number().equals(user)){
                return i;
            }
            i++;
        }
        return -1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.accept_incomming_video_call_btn:
                Intent intent = new Intent(IncommingVideoCallActivity.this, IncommingVideoCallActivity2.class);
                intent.putExtra("call_id", callID);
                startActivity(intent);
                finish();
                break;
            case R.id.decline_incomming_video_call_btn:
                stringeeCall.hangup();
                finish();
                break;
            default: break;
        }
    }
}
