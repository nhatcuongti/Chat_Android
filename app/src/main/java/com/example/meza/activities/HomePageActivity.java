package com.example.meza.activities;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.meza.ActivePeopleFragment;
import com.example.meza.ChatsFragment;
import com.example.meza.R;
import com.example.meza.model.ConversationModel;
import com.example.meza.model.User;
import com.example.meza.utilities.GenAccessToken;
import com.example.meza.utils.Utils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stringee.StringeeClient;
import com.stringee.call.StringeeCall;
import com.stringee.call.StringeeCall2;
import com.stringee.exception.StringeeError;
import com.stringee.listener.StringeeConnectionListener;

import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomePageActivity extends FragmentActivity {
    TextView fragmentName;

    ImageButton chatsBtn, activePeopleBtn;
    CircleImageView circleImageView;

    ChatsFragment chatsFragment;
    ActivePeopleFragment activePeopleFragment;

    String [] permissions = {Manifest.permission.RECORD_AUDIO};
    Boolean permissionToRecordAccepted = false;

    ArrayList<User> listActiveUser;
    ArrayList<String> listFriend;
    ArrayList<User> listObjectUserFriend;
    ArrayList<ConversationModel> listRecentConversation;

    private DatabaseReference mDatabase;
    private User currentUser;
    private String userID;

    StringeeClient client;
    String stringeeToken;
    String KEYID = "SKyXNWgIwlhTrsWvXt8DRDcSvenugYiXjz";;
    String KEYSECRET = "REd3YnBkYW5BM2dVYmdCRlExdHZTWWhNVTYzZmtyY1o=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        intData();
//        initSinch();
        initStringeeClient();
        fragmentName = findViewById(R.id.name_fragment);
        chatsBtn = (ImageButton) findViewById(R.id.chats_Button);
        activePeopleBtn = (ImageButton) findViewById(R.id.active_people_Button);
        circleImageView = findViewById(R.id.avatar);

        chatsFragment = new ChatsFragment(this, listActiveUser, listRecentConversation, listObjectUserFriend);
        activePeopleFragment = new ActivePeopleFragment(this,listActiveUser, listRecentConversation);

        replaceFragment(chatsFragment);


        // decode base64 string to bitmap image and set image for imageview
        if (currentUser.getImage() != null) {
            circleImageView.setImageBitmap(Utils.decodeImage(currentUser.getImage()));
        }

        // chuyen sang man hinh setting, chua truyen du lieu
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, SettingActivity.class);
                intent.putExtra("currentUser", currentUser);
                startActivity(intent);
            }
        });
        chatsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(chatsFragment);
                fragmentName.setText("Chats");
            }
        });
        activePeopleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(activePeopleFragment);
                fragmentName.setText("Active People");
            }
        });
        this.requestPermissions(permissions,200);
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_chats, fragment);
        fragmentTransaction.commit();
    }

    public void intData() {
        currentUser = User.getCurrentUser(this);
        userID = currentUser.getId();
        listFriend = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference listFriendRef = mDatabase.child("users").child(userID).child("list_friend");
        listFriendRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listFriend.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String friendID = (String) ds.getValue(String.class);
                    Log.i(ContentValues.TAG, "loadPost:" + friendID);
                    listFriend.add(friendID);
                }
                chatsFragment.getActiveThumnailAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        listActiveUser = new ArrayList<>();
        listObjectUserFriend = new ArrayList<>();
        DatabaseReference listActivUserRef = mDatabase.child("users");
        listActivUserRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listActiveUser.clear();
                listObjectUserFriend.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    String userKey = ds.getKey();
                    if(listFriend.contains(userKey))
                        listObjectUserFriend.add(user);
                    if (user.getIs_active() == 1 && listFriend.contains(userKey)) {
                        listActiveUser.add(user);
                    }
                }
                chatsFragment.getActiveThumnailAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listRecentConversation = new ArrayList<>();
        DatabaseReference conversationRef = mDatabase.child("conversation");
        conversationRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ConversationModel conversationModel = snapshot.getValue(ConversationModel.class);
                Log.d("abcxyz", "onChildAdded: " + conversationModel.toString());
                if (conversationModel.getParticipant_list().get(userID) != null) {

                    listRecentConversation.add(conversationModel);
                    chatsFragment.getNameOfConversationAdapter().notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                ConversationModel conversationModel = snapshot.getValue(ConversationModel.class);
                if (conversationModel.getParticipant_list().get(userID)) {
                    for (ConversationModel cv : listRecentConversation)
                        if (cv.getID().equals(conversationModel.getID()))
                            listRecentConversation.remove(conversationModel);

                    chatsFragment.getNameOfConversationAdapter().notifyDataSetChanged();
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        listRecentConversation.add("Linh Giang");
//        listRecentConversation.add("Thu Nga");
//        listRecentConversation.add("Quỳnh Hương");
//        listRecentConversation.add("Hữu Long");
//        listRecentConversation.add("Ngọc Luân");
//        listRecentConversation.add("Nhật Anh");
//        listRecentConversation.add("Bảo Trung");
//        listRecentConversation.add("Hoàng Nhật");
//        listRecentConversation.add("Hữu Toàn");
//        listRecentConversation.add("Việt Hùng");
//        listRecentConversation.add("Bảo Long");
//        listRecentConversation.add("Công Lượng");
    }

//    public void initSinch() {
//
//// Instantiate a SinchClient using the SinchClientBuilder.
//        android.content.Context context = this.getApplicationContext();
//        sinchClient = Sinch.getSinchClientBuilder()
//                .context(context)
//                .applicationKey("b0274bc0-fb51-4fae-b3eb-5d75b673c442")
//                .environmentHost("ocra.api.sinch.com")
//                .userId(userID)
//                .build();
//
////        sinchClient.setSupportManagedPush(true);
//        sinchClient.startListeningOnActiveConnection();
//        sinchClient.addSinchClientListener(new MySinchClientListener());
//        sinchClient.start();
//
//        sinchClient.getCallClient().setRespectNativeCalls(false);
//        sinchClient.getCallClient().addCallClientListener(new MyCallClientListener());
//
//        Utils.sinchClient = sinchClient;
//
//    }

//    private class MySinchClientListener implements SinchClientListener{
//
//        @Override
//        public void onClientStarted(SinchClient sinchClient) {
//            Log.d("failed", "client started ");
//
//        }
//
//        @Override
//        public void onClientFailed(SinchClient sinchClient, SinchError sinchError) {
//            sinchClient.terminateGracefully();
//            Utils.sinchClient = null;
//        }
//
//        @Override
//        public void onLogMessage(int i, String s, String s1) {
//
//        }
//
//        @Override
//        public void onPushTokenRegistered() {
//            Log.d("failed", "push fffffffffffffffffffffffffffffffff ");
//
//        }
//
//        @Override
//        public void onPushTokenRegistrationFailed(SinchError sinchError) {
//            Log.d("failed", "onPushTokenRegistrationFailed "+ sinchError.getMessage()+ " " + sinchError.getCode());
//
//        }
//
//        @Override
//        public void onCredentialsRequired(ClientRegistration clientRegistration) {
//            String jwt = JWT.create("b0274bc0-fb51-4fae-b3eb-5d75b673c442"
//                    , "aROplhftr0CC4+loLEN7RA=="
//                    , userID);
//            clientRegistration.register(jwt);
//        }
//
//        @Override
//        public void onUserRegistered() {
//            Log.d("failed", "onUserRegistered ");
//
//        }
//
//        @Override
//        public void onUserRegistrationFailed(SinchError sinchError) {
//            Log.d("failed", "onUserRegistrationFailed "+ sinchError.getMessage()+ " " + sinchError.getCode());
//
//        }
//    }
//    private class MyCallClientListener implements CallClientListener{
//
//        @Override
//        public void onIncomingCall(CallClient callClient, Call call) {
//
//        }
//    }
//    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode){
//            case REQUEST_RECORD_AUDIO_PERMISSION:
//                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                break;
//        }
//        if (!permissionToRecordAccepted ) finish();
//    }

//    // request permission
//    private static final int PERMISSION_REQ_ID = 22;
//
//    private static final String[] REQUESTED_PERMISSIONS = {
//            Manifest.permission.RECORD_AUDIO,
//            Manifest.permission.CAMERA
//    };
//
//    private boolean checkSelfPermission(String permission, int requestCode) {
//        if (ContextCompat.checkSelfPermission(this, permission) !=
//                PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
//            return false;
//        }
//        return true;
//    }
    void initStringeeClient(){
        client = new StringeeClient(this);
        Utils.stringeeClient = client;
        stringeeToken = GenAccessToken.genAccessToken(userID,KEYID, KEYSECRET, 600);
//        stringeeToken = "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTS3lYTldnSXdsaFRyc1d2WHQ4RFJEY1N2ZW51Z1lpWGp6LTE2NDkxNDgxNjAiLCJpc3MiOiJTS3lYTldnSXdsaFRyc1d2WHQ4RFJEY1N2ZW51Z1lpWGp6IiwiZXhwIjoxNjQ5MTUxNzYwLCJ1c2VySWQiOiIwMzY1ODYzODE3In0.2jYdIfADRydckJtdxSpr6S5LM9-SHSxXdpNCjnMzRZc";
        client.setConnectionListener(new StringeeConnectionListener() {
            @Override
            public void onConnectionConnected(final StringeeClient stringeeClient, boolean isReconnecting) {
                Log.d("clientCon", "connected " + isReconnecting);
            }
            @Override
            public void onConnectionDisconnected(StringeeClient stringeeClient, boolean isReconnecting) {
                Log.d("clientCon", "disconnected " + isReconnecting);
            }
            @Override
            public void onIncomingCall(final StringeeCall stringeeCall) {
            }
            @Override
            public void onIncomingCall2(StringeeCall2 stringeeCall2) {
            }
            @Override
            public void onConnectionError(StringeeClient stringeeClient, final StringeeError stringeeError) {
                Log.d("clientCon", "conerror " + stringeeError.toString());

            }
            @Override
            public void onRequestNewToken(StringeeClient stringeeClient) {
                // Get new token here and connect to Stringe server
                stringeeToken = GenAccessToken.genAccessToken(userID,KEYID, KEYSECRET, 600);
                Log.d("clientCon", "token " );
            }
            @Override
            public void onCustomMessage(String s, JSONObject jsonObject) {
            }
            @Override
            public void onTopicMessage(String s, JSONObject jsonObject) {
            }
        });
        Log.d("clientCon", "token " + stringeeToken);
        client.connect(stringeeToken);
        Log.d("clientCon", "iscon " + client.isConnected());
    }

    public interface ItemClickListener {

        void onClick(View view, int position, boolean isLongClick);
    }
}