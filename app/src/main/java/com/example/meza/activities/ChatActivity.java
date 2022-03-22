package com.example.meza.activities;


import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meza.R;
import com.example.meza.adapters.ConversationAdapter;
import com.example.meza.databinding.ActivityChatBinding;
import com.example.meza.databinding.BottombarChatBinding;
import com.example.meza.databinding.ToolbarChatBinding;
import com.example.meza.interfaces.OnGetValueListener;
import com.example.meza.model.ConversationModel;
import com.example.meza.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;


public class ChatActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    RecyclerView conversationRv;
    ConversationAdapter conversationAdapter;
    ArrayList<String> participantList = new ArrayList<>();
    User currentUser;
    EditText chatBox ;
    ImageButton rightArrowBtn, imageSendBtn, takePhotoBtn, voiceSendBtn, sendBtn, videoCallBtn, audioCallBtn, inforBtn;
    ConversationModel conversation;
    String idConversation;
    ProgressBar progressBar;

    ActivityChatBinding chatBinding;
    ToolbarChatBinding toolbarChatBinding;
    BottombarChatBinding bottombarChatBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        progressBar = findViewById(R.id.progressBar);
        conversationRv = findViewById(R.id.conversationListRv);
        loading(true);
        initData();
        initButton();
    }


    /**
     * setDataOnConversation() là hàm dùng để set dữ liệu vào thanh topbar đầu tiên
     */
    public void setDataOnConversation(){
        // Set ảnh đối tác
        // Set tên nhóm / tên đối tác
        TextView tittle = findViewById(R.id.tittleName);
        tittle.setText(conversation.getTittle());
    }

    /**
     * initData() là hàm dùng để khởi tạo dữ liệu ban đầu
     * Mục đích của hàm này là tìm ID của Conversation
     */
    public void initData(){
        //**************************************************************************************
                                    //Lấy dữ liệu của user hiện tại//
        currentUser = User.getCurrentUser(this);
        //************************************End***********************************************

        //**************************************************************************************
                                    //Lấy dữ liệu của conversation hiện tại/
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        idConversation = bundle.getString("conversationID");
        //************************************End***********************************************


        //**************************************************************************************
                                    //Xử lý ở RecycleView, lắng nghe tin nhắn từ database//
        ConversationModel.getFirstConversationWithID(idConversation, new OnGetValueListener() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                conversation = snapshot.getValue(ConversationModel.class);
                conversation.formatParticipantList();
                Log.d("abcdef", "onSuccess: " + conversation.getLast_time());
                if (conversation.getLast_time() == -1) // Nếu như chưa có tin nhắn
                    loading(false);
                setDataOnConversation();

                //**************************************************************************************
                                    //Tạo RecycleView và Adapter//

                //Tạo Adapter và set Adapter cho conversationRV
                conversationAdapter = new ConversationAdapter(ChatActivity.this, conversation, currentUser);
                conversationRv.setAdapter(conversationAdapter);
                // Set LayoutManager
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
                conversationRv.setLayoutManager(linearLayoutManager);

                conversationRv.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                        if (i3 < i7){
                            conversationRv.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ArrayList<ConversationModel.Message> listMsg = conversation.getListMessage();
                                    if (listMsg != null && !listMsg.isEmpty())
                                        conversationRv.smoothScrollToPosition(
                                                conversationRv.getAdapter().getItemCount() - 1);
                                }
                            }, 100);
                        }
                    }
                });
                //************************************End***********************************************


                //**************************************************************************************
                                    //Lấy các đoạn tin nhắn của đoạn hội thoại, đồng thời
                                    //lắng nghe nếu có tin nhắn mới

                ConversationModel.Message.listenChange(idConversation, new OnGetValueListener() {
                    @Override
                    public void onSuccess(DataSnapshot snapshot) {
                        loading(false);
                        ConversationModel.Message msg = snapshot.getValue(ConversationModel.Message.class);
                        msg.formatStartTime();

                        ArrayList<ConversationModel.Message> list_message = conversation.getListMessage();
                        if (list_message == null)
                            list_message = new ArrayList<>();

                        list_message.add(msg);
                        conversation.setListMessage(list_message);
                        conversationAdapter.notifyDataSetChanged();
                        conversationRv.scrollToPosition(list_message.size() - 1);
                    }
                });
                //************************************End***********************************************

            }
        });
        //************************************End***********************************************

    }

    /**
     * initButton() là hàm dùng để khởi tạo các button và set Click Listener cho nó
     */
    public void initButton(){
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
                break;
            }

            case R.id.imageSendBtn:{ // Nếu người dùng bấm nút gửi ảnh từ thư viện ảnh
                break;
            }

            case R.id.voiceSendBtn:{ // Nếu người dùng bấm nút gửi đoạn record
                break;
            }

            case R.id.audioCallBtn:{ // Nếu người dùng bấm nút gọi điện theo dạng audio
                break;
            }

            case R.id.inforBtn:{ // Nếu người dùng bấm nút infor
                break;
            }

            case R.id.videoCallBtn:{ // Nếu người dùng bấm nút video call
                break;
            }

            case R.id.takePhotoBtn:{ // Nếu người dùng bấm nút chụp ảnh và gửi lên đoạn hội thoại
                break;
            }

            case R.id.sendBtn:{ // Nếu như người dùng bấm nút gửi tin nhắn

                //********************************************************************************//
                                        //Khởi tạo Message mà người dùng vừa gửi//
                String text = chatBox.getText().toString();
                ConversationModel.Message msg = new ConversationModel.Message();
                msg.setTimestamp(System.currentTimeMillis());
                msg.setSender(currentUser.getId());
                msg.setText(text);
                //*********************************End********************************************//



                //********************************************************************************//
                                        //Gửi đoạn tin nhắn lên database//
                ConversationModel.Message.listenLastElement(idConversation, new OnGetValueListener() { // Cập nhật tin nhắn
                    @Override
                    public void onSuccess(DataSnapshot snapshot) {
                        //************************************************************************//
                                            //Generate ra ID mới dựa trên ID lớn nhất//
                        DataSnapshot lastSnapshot = null;
                        for (DataSnapshot ds : snapshot.getChildren())
                            lastSnapshot = ds;

                        String id = (lastSnapshot == null) ? "0" : lastSnapshot.getKey();
                        Log.d("test", "onSuccess: " + id);
                        id = String.valueOf(Integer.valueOf(id) + 1);
                        //*********************************End************************************//

                        msg.setId(id);
                        ConversationModel.Message.insertNewMsgToDatabase(msg, id, idConversation);
                        chatBox.setText("");
                    }
                });


                // Cập nhật conversation
                conversation.setLast_message(msg.getText());
                conversation.setLast_time(msg.getTimestamp());
                ConversationModel.updateConversation(idConversation, conversation.toMap());
                break;
                //*********************************End********************************************//

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

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            conversationRv.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            conversationRv.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

}
