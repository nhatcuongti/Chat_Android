package com.example.meza.activities;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.example.meza.R;
import com.example.meza.fragment.ActivePeopleFragment;
import com.example.meza.fragment.ChatsFragment;
import com.example.meza.model.ConversationModel;
import com.example.meza.model.User;
import com.example.meza.utilities.Constants;
import com.example.meza.utilities.GenAccessToken;
import com.example.meza.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
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

    FloatingActionButton addFriendBtn;
    ImageButton chatsBtn, activePeopleBtn;
    CircleImageView circleImageView;

    ChatsFragment chatsFragment;
    ActivePeopleFragment activePeopleFragment;


    String [] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};

    Boolean permissionToRecordAccepted = false;

    ArrayList<User> listActiveUser;
    ArrayList<String> listFriend;
    static ArrayList<User> listObjectUserFriend;
    ArrayList<ConversationModel> listRecentConversation;

    private DatabaseReference mDatabase;
    private User currentUser;
    private String userID;
    private String callerId;

    StringeeClient client;
    String stringeeToken;
    String KEYID = "SKyXNWgIwlhTrsWvXt8DRDcSvenugYiXjz";
    String KEYSECRET = "REd3YnBkYW5BM2dVYmdCRlExdHZTWWhNVTYzZmtyY1o=";
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        intData();

        updateToken();
//
        initStringeeClient();
        fragmentName = findViewById(R.id.name_fragment);
        chatsBtn = (ImageButton) findViewById(R.id.chats_Button);
        activePeopleBtn = (ImageButton) findViewById(R.id.active_people_Button);
        circleImageView = findViewById(R.id.avatar);
        addFriendBtn = findViewById(R.id.fabNewChat);

        chatsFragment = new ChatsFragment(this, listActiveUser, listRecentConversation, listObjectUserFriend);
        activePeopleFragment = new ActivePeopleFragment(this, listActiveUser, listRecentConversation);

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
                fragmentName.setText("M???i ng?????i");
            }
        });
        addFriendBtn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), UsersActivity.class));
        });
        this.requestPermissions(permissions, 200);
    }

    private void updateToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            if (!TextUtils.isEmpty(token)) {
                Log.d("FirebaseToken", "retrieve token successful : " + token);
            } else {
                Log.w("FirebaseToken", "token should not be null...");
            }
        }).addOnFailureListener(e -> {
            //handle e
        }).addOnCanceledListener(() -> {
            //handle cancel
        }).addOnCompleteListener(task -> {
            token = task.getResult();

        });
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(Constants.KEY_COLLECTION_USERS).child(currentUser.getId()).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getRef().child("token").setValue(token);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
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
        callerId = "m" + userID;
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
                    if (listFriend.contains(userKey))
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

    }//init data


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
    void initStringeeClient() {
        client = new StringeeClient(this);
        Utils.stringeeClient = client;
        stringeeToken = GenAccessToken.genAccessToken(callerId, KEYID, KEYSECRET, 3600);
//        stringeeToken = "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTS3lYTldnSXdsaFRyc1d2WHQ4RFJEY1N2ZW51Z1lpWGp6LTE2NDk2MDg0NDEiLCJpc3MiOiJTS3lYTldnSXdsaFRyc1d2WHQ4RFJEY1N2ZW51Z1lpWGp6IiwiZXhwIjoxNjQ5NjEyMDQxLCJ1c2VySWQiOiIwMzY1ODYzODE3In0.TzoK9GssTV1fe20_LKbzzbHtYJSX_bWZkSUAz9rrJ4U";
        client = new StringeeClient(this);
        client.setConnectionListener(new StringeeConnectionListener() {
            @Override
            public void onConnectionConnected(final StringeeClient stringeeClient, boolean isReconnecting) {
                Log.d("clientCon", "onConnectionConnected: " + client.getUserId());
                Utils.stringeeClient = client;
                CallsMap.putData("client", client);

            }

            @Override
            public void onConnectionDisconnected(StringeeClient stringeeClient, boolean isReconnecting) {
            }

            @Override
            public void onIncomingCall(final StringeeCall stringeeCall) {
                Log.d("clientCon", "onIncomingCallCount: " + Utils.countInCommingCallAtMoment);
                CallsMap.putData(stringeeCall.getCallId(), stringeeCall);
                Bundle bundle = new Bundle();
                bundle.putString("callerId", stringeeCall.getFrom());
                bundle.putString("call_id", stringeeCall.getCallId());
                if(stringeeCall.isVideoCall()) {
                    Intent intent1 = new Intent(HomePageActivity.this, IncommingVideoCallActivity.class);
//                    bundle.putString("callerImage", caller.getImage());
                    intent1.putExtras(bundle);
                    startActivity(intent1);
                }
                else{
                    Utils.countInCommingCallAtMoment++;
                    if(Utils.countInCommingCallAtMoment <= 1) {
//                    User caller = listObjectUserFriend.get(findFriendById(stringeeCall.getFrom()));
                        CallsMap.putData(stringeeCall.getCallId(), stringeeCall);
                        Intent intent2 = new Intent(HomePageActivity.this, IncommingCallActivity.class);

//                    bundle.putString("callerImage", caller.getImage());
                        intent2.putExtras(bundle);
                        Log.d("clientCon", "onIncomingCall: ");
                        startActivity(intent2);
                    }
                }

            }

            @Override
            public void onIncomingCall2(StringeeCall2 stringeeCall2) {
            }

            @Override
            public void onConnectionError(StringeeClient stringeeClient, final StringeeError stringeeError) {
                Log.d("clientCon", "onConnectionError: ");
            }

            @Override
            public void onRequestNewToken(StringeeClient stringeeClient) {
                // Get new token here and connect to Stringe server
                stringeeToken = GenAccessToken.genAccessToken(callerId, KEYID, KEYSECRET, 3600);

            }

            @Override
            public void onCustomMessage(String s, JSONObject jsonObject) {
            }

            @Override
            public void onTopicMessage(String s, JSONObject jsonObject) {
            }
        });
        client.connect(stringeeToken);
    }


    int findFriendById(String userID) {
        String user = userID.substring(1);
        int i = 0;
        for (User u : listObjectUserFriend) {
            if (u.getId().equals(user)) {
                return i;
            }
            i++;
        }
        return -1;
    }

}