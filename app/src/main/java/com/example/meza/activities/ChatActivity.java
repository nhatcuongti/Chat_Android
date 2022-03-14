package com.example.meza.activities;


import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meza.R;
import com.example.meza.adapters.ConversationAdapter;
import com.example.meza.adapters.ConversationFixAdapter;
import com.example.meza.model.ConversationModel;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;


public class ChatActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    RecyclerView conversationRv;
    ArrayList<ConversationModel.Message> msgList = new ArrayList<>();
    EditText chatBox ;
    ImageButton rightArrowBtn, imageSendBtn, takePhotoBtn, voiceSendBtn, sendBtn, videoCallBtn, audioCallBtn, inforBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        conversationRv = findViewById(R.id.conversationListRv);
        //Lấy dữ liệu thô
        initData();
        ConversationModel conversation = new ConversationModel("Hiếu Lê",  R.drawable.hieule, true, msgList);
        //Tạo Adapter và set Adapter cho conversationRV
        conversationRv.setAdapter(new ConversationFixAdapter(this, conversation));
        // Set LayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        conversationRv.setLayoutManager(linearLayoutManager);

        // Xử lí sự kiện của các nút nhấn, EditText
        chatBox = findViewById(R.id.chatboxEdit);
        rightArrowBtn = findViewById(R.id.rightArrowBtn);
        rightArrowBtn.setOnClickListener(this);

        imageSendBtn = findViewById(R.id.imageSendBtn);
        imageSendBtn.setOnClickListener(this);
        imageSendBtn.setOnTouchListener(this);

        takePhotoBtn = findViewById(R.id.takePhotoBtn);
        takePhotoBtn.setOnClickListener(this);
        takePhotoBtn.setOnTouchListener(this);


        voiceSendBtn = findViewById(R.id.voiceSendBtn);
        voiceSendBtn.setOnClickListener(this);
        voiceSendBtn.setOnTouchListener(this);


        sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this);
        sendBtn.setOnTouchListener(this);

        videoCallBtn = findViewById(R.id.videoCallBtn);
        videoCallBtn.setOnClickListener(this);
        videoCallBtn.setOnTouchListener(this);

        audioCallBtn = findViewById(R.id.audioCallBtn);
        audioCallBtn.setOnClickListener(this);
        audioCallBtn.setOnTouchListener(this);

        inforBtn = findViewById(R.id.inforBtn);
        inforBtn.setOnClickListener(this);
        inforBtn.setOnTouchListener(this);

        handleEvent();
    }

    public void initData(){
        //Dữ liệu cần phải có : (Giả sử chat với người A)
        // - Danh sách tất cả đoạn chat của mình với người A
        // - Danh sách tất cả đoạn chat của người A với Mình
        // => Cùng một loại dữ liệu, đặt tên là conversation

        //Conversation gồm có :
        // - UserID (Mình)
        // - partnerID(Đối phương) (Từ đây sẽ biết được Name, Image và tình trạng hoạt động)
        // - ArrayList<Messenger> (Messenger gồm : ISend = true nếu mình gửi, false nếu đối tác gửi và đoạn tin nhắn, thể loại)

        msgList.add(new ConversationModel.Message(true,
                "Hello",
                LocalDateTime.of(2022, Month.FEBRUARY, 20, 13, 45, 30)));

        msgList.add(new ConversationModel.Message(false,
                "Hi ! What's Your Name ?",
                LocalDateTime.of(2022, Month.FEBRUARY, 20, 13, 45, 40)));

        msgList.add(new ConversationModel.Message(false,
                "Hi ! bla bla bla bla ?",
                LocalDateTime.of(2022, Month.FEBRUARY, 20, 13, 46, 40)));

        msgList.add(new ConversationModel.Message(true,
                "My Name's Hao , How about you ?",
                LocalDateTime.of(2022, Month.FEBRUARY, 20, 13, 48, 40)));

        msgList.add(new ConversationModel.Message(false,
                "Hieu. What's your favorite ?",
                LocalDateTime.of(2022, Month.FEBRUARY, 20, 13, 49, 40)));

        msgList.add(new ConversationModel.Message(true,
                "My Favorite is Football, I like watching EPL especially match having Man city, Do you know Man City",
                LocalDateTime.of(2022, Month.FEBRUARY, 20, 13, 50, 40)));

        msgList.add(new ConversationModel.Message(true,
                "Giờ đang phân vân 3 hãng HP, Lenovo với Thinkpad luôn mà éo biết lấy thằng nào",
                LocalDateTime.of(2022, Month.FEBRUARY, 20, 13, 50, 52)));

        msgList.add(new ConversationModel.Message(false,
                "bla bla bla \n bla bla bla \n bla bla",
                LocalDateTime.of(2022, Month.FEBRUARY, 20, 13, 51, 40)));

        msgList.add(new ConversationModel.Message(false,
                "Are you ok With that",
                LocalDateTime.of(2022, Month.FEBRUARY, 20, 14, 53, 40)));

        msgList.add(new ConversationModel.Message(false,
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s",
                LocalDateTime.of(2022, Month.FEBRUARY, 20, 15, 55, 40)));

        msgList.add(new ConversationModel.Message(true,
                "Hello man",
                LocalDateTime.now()));

        msgList.add(new ConversationModel.Message(true,
                "Alo A Hieu",
                LocalDateTime.now()));
    }

    public void handleEvent(){
        chatBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str = chatBox.getText().toString();

                if (!str.equals("")){
                    rightArrowBtn.setVisibility(View.VISIBLE);
                    sendBtn.setVisibility(View.VISIBLE);

                    imageSendBtn.setVisibility(View.GONE);
                    takePhotoBtn.setVisibility(View.GONE);
                    voiceSendBtn.setVisibility(View.GONE);
                }
                else{
                    rightArrowBtn.setVisibility(View.GONE);
                    sendBtn.setVisibility(View.GONE);

                    imageSendBtn.setVisibility(View.VISIBLE);
                    takePhotoBtn.setVisibility(View.VISIBLE);
                    voiceSendBtn.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rightArrowBtn:{
                rightArrowBtn.setVisibility(View.GONE);
                sendBtn.setVisibility(View.GONE);

                imageSendBtn.setVisibility(View.VISIBLE);
                takePhotoBtn.setVisibility(View.VISIBLE);
                voiceSendBtn.setVisibility(View.VISIBLE);
            }

            case R.id.imageSendBtn:{

            }

            case R.id.voiceSendBtn:{

            }

            case R.id.audioCallBtn:{

            }

            case R.id.inforBtn:{

            }

            case R.id.videoCallBtn:{

            }

            case R.id.takePhotoBtn:{

            }

            case R.id.sendBtn:{

            }


        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                view.getBackground().setColorFilter(0xe0f47521,PorterDuff.Mode.SRC_ATOP);
                view.invalidate();
                break;
            }
            case MotionEvent.ACTION_UP: {
                view.getBackground().clearColorFilter();
                view.invalidate();
                break;
            }
        }

        return false;
    }
}