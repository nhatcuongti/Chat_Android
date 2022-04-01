package com.example.meza.activities;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meza.R;
import com.example.meza.adapters.ConversationAdapter;
import com.example.meza.databinding.ActivityChatBinding;
import com.example.meza.databinding.BottombarChatBinding;
import com.example.meza.databinding.ToolbarChatBinding;
import com.example.meza.interfaces.OnGetImageClickListener;
import com.example.meza.interfaces.OnGetValueListener;
import com.example.meza.model.ConversationModel;
import com.example.meza.model.User;
import com.example.meza.utilities.Constants;
import com.example.meza.utils.Utilss;
import com.google.firebase.database.DataSnapshot;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatActivity extends AppCompatActivity implements View.OnClickListener,
        View.OnTouchListener,
        OnGetImageClickListener {

    RecyclerView conversationRv;
    ConversationAdapter conversationAdapter;
    ArrayList<ConversationModel.Message> participantList = new ArrayList<>();
    User currentUser;
    EditText chatBox ;
    ImageButton backwardBtn, rightArrowBtn, imageSendBtn, takePhotoBtn, voiceSendBtn, sendBtn, videoCallBtn, audioCallBtn, inforBtn;
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

        CircleImageView userImage = findViewById(R.id.userImage);
        CircleImageView userActive = findViewById(R.id.userActive);

        HashMap<String, Bitmap> user_image = conversation.getUser_image();
        for (String userID : conversation.getParticipantListArray())
            if (!userID.equals(currentUser.getId())){
                userImage.setImageBitmap(user_image.get(userID));
            }
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
                if (conversation.getLast_time() == -1) // Nếu như chưa có tin nhắn
                    loading(false);

                //**************************************************************************************
                                    //Tạo RecycleView và Adapter//

                //Tạo Adapter và set Adapter cho conversationRV
                conversation.formatParticipantList(ChatActivity.this, new OnGetValueListener() {
                    @Override
                    public void onSuccess(DataSnapshot snapshot) {
                        setDataOnConversation();
                        conversationAdapter = new ConversationAdapter(ChatActivity.this, conversation, currentUser);
                        conversationRv.setAdapter(conversationAdapter);
                    }

                    @Override
                    public void onChange(DataSnapshot snapshot) {

                    }
                });
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
                    public void onSuccess(DataSnapshot snapshot) { // Nếu một tin nhắn thêm mới thành công
                        loading(false);
                        ConversationModel.Message msg = snapshot.getValue(ConversationModel.Message.class);
                        msg.formatStartTime();

                        ArrayList<ConversationModel.Message> list_message = conversation.getListMessage();
                        if (list_message == null)
                            list_message = new ArrayList<>();


                        //**************************************************************************
                                            // Xử lý phần seen//
                        Map<String, Boolean> listSeen = msg.getListSeen();
                        if (listSeen == null)
                            listSeen = new HashMap<>();

                        if (!msg.getSender().equals(currentUser.getId()))
                            listSeen.put(currentUser.getId(), true);

                        msg.setListSeen(listSeen);
                        //************************************End***********************************
                        list_message.add(msg);
                        conversation.setListMessage(list_message);
                        conversationAdapter.notifyDataSetChanged();
                        conversationRv.scrollToPosition(list_message.size() - 1);

                        ConversationModel.Message.updateMessage(idConversation, msg.getId(), msg.toMap());
                    }

                    @Override
                    public void onChange(DataSnapshot snapshot) {
                        ConversationModel.Message message = snapshot.getValue(ConversationModel.Message.class);
                        message.formatStartTime();

                        ArrayList<ConversationModel.Message> listMsg = conversation.getListMessage();

                        int lastElement = listMsg.size() - 1;
                        if (listMsg.get(lastElement).getId().equals(message.getId()))
                            listMsg.set(lastElement, message);

                        conversationAdapter.notifyDataSetChanged();
                    }
                });
                //************************************End***********************************************

            }

            @Override
            public void onChange(DataSnapshot snapshot) {

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

        backwardBtn = findViewById(R.id.backwardBtn);
        backwardBtn.setOnClickListener(this);

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
            case R.id.backwardBtn:{
                finish();
            }

            case R.id.rightArrowBtn:{
                rightArrowBtn.setVisibility(View.GONE);
                sendBtn.setVisibility(View.GONE);

                imageSendBtn.setVisibility(View.VISIBLE);
                takePhotoBtn.setVisibility(View.VISIBLE);
                voiceSendBtn.setVisibility(View.VISIBLE);
                break;
            }

            case R.id.imageSendBtn:{ // Nếu người dùng bấm nút gửi ảnh từ thư viện ảnh
                //Tạo một intent để có thể truy cập được tới gallery và chọn ảnh
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
                break;
            }

            case R.id.voiceSendBtn:{ // Nếu người dùng bấm nút gửi đoạn record

                break;
            }

            case R.id.audioCallBtn:{ // Nếu người dùng bấm nút gọi điện theo dạng audio
                Intent intent = new Intent(ChatActivity.this, VoiceCallingActivity.class);
                startActivity(intent);
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
                msg.setTypeMessage(Constants.KEY_TEXT);
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

                    @Override
                    public void onChange(DataSnapshot snapshot) {

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

    /**
     * onActivityResult() là hàm dùng để xử lý sau sự kiện chọn ảnh trong gallery
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && requestCode == 1){
            Uri uri = data.getData();

            try {
                InputStream is = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                String encodeString = Utilss.encodeImageForSend(bitmap);

                //********************************************************************************//
                            //Khởi tạo Message mà người dùng vừa gửi//
                ConversationModel.Message msg = new ConversationModel.Message();
                msg.setTimestamp(System.currentTimeMillis());
                msg.setSender(currentUser.getId());
                msg.setText(encodeString);
                msg.setTypeMessage(Constants.KEY_IMAGE);
                //*********************************End********************************************//

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

                    @Override
                    public void onChange(DataSnapshot snapshot) {

                    }
                });

                // Cập nhật conversation
                conversation.setLast_message("Hình ảnh vừa được gửi");
                conversation.setLast_time(msg.getTimestamp());
                ConversationModel.updateConversation(idConversation, conversation.toMap());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
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

    /**
     * dispatchTouchEvent() là hàm dùng để lắng nghe khi click ngoài thanh chat box thì ta sẽ clear
     * focus của nó
     * @param event
     * @return
     */
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

    /**
     * loading() là hàm dùng để thay đổi vòng load Progress Bar
     * @param isLoading
     */
    private void loading(Boolean isLoading) {
        if (isLoading) {
            conversationRv.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            conversationRv.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * onGetImageClick() là hàm lắng nghe sự kiện click vào một image trong đoạn tin nhắn để
     * xem ảnh ở trạng thái full screen
     * @param encodeImage
     */
    @Override
    public void onGetImageClick(String encodeImage) {
        Intent intent = new Intent(ChatActivity.this, FullImageScreenActivity.class);
        Bitmap bm = Utilss.decodeImage(encodeImage);
        String filePath = Utilss.tempFileImage(ChatActivity.this, bm, Constants.KEY_IMAGE);
        intent.putExtra(Constants.KEY_IMAGE, filePath);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ConversationModel.Message.chatReference.removeEventListener(ConversationModel.Message.childEventListener);
    }
}
