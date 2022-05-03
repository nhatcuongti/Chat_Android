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
import android.text.Layout;
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
import com.example.meza.interfaces.PaginationScrollListener;
import com.example.meza.model.ConversationModel;
import com.example.meza.model.User;
import com.example.meza.utilities.Constants;
import com.example.meza.utils.Utils;
import com.google.firebase.database.DataSnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
    ProgressBar progressBar, progressBarLoadData;
    HashMap<String, Boolean> mesID;

    ActivityChatBinding chatBinding;
    ToolbarChatBinding toolbarChatBinding;
    BottombarChatBinding bottombarChatBinding;

    HashMap<String, Bitmap> user_image;


    String token = "";
    String urlStr = "";
    String urlBase = "https://mezatoken.herokuapp.com";
    String channelName = "test";
    String curUserID;
    Bundle myBundle;

    String calleeId; // nguoi duoc goi
    String calleeName;
    Bitmap calleeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        channelName = bundle.getString("conversationID");

        progressBar = findViewById(R.id.progressBar);
        progressBarLoadData = findViewById(R.id.progressBarLoadData);
        conversationRv = findViewById(R.id.conversationListRv);

        curUserID = User.getCurrentUser(getApplicationContext()).getPhone_number();
        urlStr = urlBase + "/rtc/" + "meza" + channelName + "/publisher/uid/"  + channelName  + "/";
        Log.d("channel name", channelName);
        Log.d("url", urlStr);
        new fetchData(token, urlStr).start();

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
        calleeName = conversation.getTittle();

        CircleImageView userImage = findViewById(R.id.userImage);
        CircleImageView userActive = findViewById(R.id.userActive);

        user_image = conversation.getUser_image();
        for (String userID : conversation.getParticipantListArray()) {
            if (!userID.equals(currentUser.getId())) {
                userImage.setImageBitmap(user_image.get(userID));
                if(conversation.getParticipant_list().size() == 2){
                    calleeId = "m" + userID;
                    calleeImage = user_image.get(userID);

                }
            }
        }

    }

    /**
     * initData() là hàm dùng để khởi tạo dữ liệu ban đầu
     * Mục đích của hàm này là tìm ID của Conversation
     */
    boolean isLastPage = false, isLoading = false;
    public void initData(){
        //**************************************************************************************
                                    //Lấy dữ liệu của user hiện tại//
        currentUser = User.getCurrentUser(this);
        //************************************End***********************************************

        //**************************************************************************************
                                    //Lấy dữ liệu của conversation hiện tại/

        idConversation = channelName;
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
                conversationRv.setHasFixedSize(true);

                conversationRv.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                        if (i3 < i7){
                            conversationRv.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ArrayList<ConversationModel.Message> listMsg = conversation.getListMessage();
                                    if (listMsg != null && !listMsg.isEmpty()){
                                        Log.d("ScrollPage", "Bottom : " + i3 + "\nOld Bottom : " + i7);
                                        conversationRv.smoothScrollToPosition(
                                                conversationRv.getAdapter().getItemCount() - 1);
                                    }
                                }
                            }, 100);
                        }
                    }
                });

                //Xử lý sự kiện scroll (pagination)
                conversationRv.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
                    @Override
                    public void loadMoreItems() {
//                        progressBarLoadData.setVisibility(View.VISIBLE);
                        isLoading = true;
                        conversationAdapter.addLoading();

                        ConversationModel.Message.listenMessageAtOffset(conversation.getListMessage().get(1).getId(),
                                idConversation,
                                new OnGetValueListener() {
                                    @Override
                                    public void onSuccess(DataSnapshot snapshot) {
                                        if (!snapshot.hasChildren()){
                                            isLastPage = true;
                                            isLoading = false;
                                            Log.d("LastPageCheck", "onSuccess: " + "last page");
                                            conversationAdapter.removeLoading();
                                            return;
                                        }

                                        int index = 0;
                                        conversationAdapter.removeLoading();
                                        for (DataSnapshot childSnapshot : snapshot.getChildren()){
                                            ConversationModel.Message msg = childSnapshot.getValue(ConversationModel.Message.class);
                                            msg.formatStartTime();
                                            ArrayList<ConversationModel.Message> list_message = conversation.getListMessage();

                                            //**************************************************************************
                                            // Xử lý phần seen//
                                            Map<String, Boolean> listSeen = msg.getListSeen();
                                            if (listSeen == null)
                                                listSeen = new HashMap<>();

                                            if (!msg.getSender().equals(currentUser.getId()))
                                                listSeen.put(currentUser.getId(), true);

                                            msg.setListSeen(listSeen);
                                            //************************************End***********************************

                                            ConversationModel.Message.updateMessage(idConversation, msg.getId(), msg.toMap());
                                            list_message.add(index, msg);
                                            conversation.setListMessage(list_message);
                                            conversationAdapter.notifyItemInserted(index);
                                            index++;
                                        }

                                        isLoading = false;
                                    }

                                    @Override
                                    public void onChange(DataSnapshot snapshot) {

                                    }
                                });
                    }

                    @Override
                    public boolean isLoading() {
                         return isLoading;
                    }

                    @Override
                    public boolean isLastPage() {
                        return isLastPage;
                    }
                });

                //************************************End***********************************************


                //**************************************************************************************
                                    //Lấy các đoạn tin nhắn của đoạn hội thoại, đồng thời
                                    //lắng nghe nếu có tin nhắn mới

                ConversationModel.Message.listenFirstMessage(idConversation, new OnGetValueListener() {
                    @Override
                    public void onSuccess(DataSnapshot snapshot) {
                        loading(false);
                        for (DataSnapshot childSnapshot : snapshot.getChildren()){
                            ConversationModel.Message msg = childSnapshot.getValue(ConversationModel.Message.class);
                            msg.formatStartTime();

                            ArrayList<ConversationModel.Message> list_message = conversation.getListMessage();
                            if (list_message == null)
                                list_message = new ArrayList<>();

                            //**************************************************************************
                            // Xử lý phần seen//
                            Map<String, Boolean> listSeen = msg.getListSeen();
                            if (listSeen == null)
                                listSeen = new HashMap<>();


                            Boolean isCurrentUserExists = (listSeen.get(currentUser.getId()) != null) ? true : false;

                            if (!msg.getSender().equals(currentUser.getId()))
                                listSeen.put(currentUser.getId(), true);

                            msg.setListSeen(listSeen);
                            //************************************End***********************************

                            list_message.add(msg);
                            conversation.setListMessage(list_message);
                            conversationAdapter.notifyItemInserted(list_message.size() - 1);
                            conversationRv.scrollToPosition(list_message.size() - 1);

                            if (!msg.getSender().equals(currentUser.getId()) && !isCurrentUserExists)
                                ConversationModel.Message.updateMessage(idConversation, msg.getId(), msg.toMap());

                            Log.d("firstMessage", list_message.toString());
                        }

                        ArrayList<ConversationModel.Message> list_message = conversation.getListMessage();
                        String lastID = list_message.get(list_message.size() - 1).getId();
                        ConversationModel.Message.listenChange(idConversation, lastID, new OnGetValueListener() {
                            @Override
                            public void onSuccess(DataSnapshot snapshot) { // Nếu một tin nhắn thêm mới thành công
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
                                conversationAdapter.notifyItemInserted(list_message.size() - 1);
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

//                                conversationAdapter.notifyDataSetChanged();
                                conversationAdapter.notifyItemChanged(lastElement);
                            }
                        });

                        ConversationModel.Message.listenChangTest(idConversation, lastID, new OnGetValueListener() {
                            @Override
                            public void onSuccess(DataSnapshot snapshot) {

                            }

                            @Override
                            public void onChange(DataSnapshot snapshot) {

                            }
                        });
                    }

                    @Override
                    public void onChange(DataSnapshot snapshot) {

                    }
                });

//                ArrayList<ConversationModel.Message> list_message = conversation.getListMessage();
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
                isStoppedByImage = true;
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
                break;
            }

            case R.id.voiceSendBtn:{ // Nếu người dùng bấm nút gửi đoạn record
                break;
            }

            case R.id.audioCallBtn:{ // Nếu người dùng bấm nút gọi điện theo dạng audio
                // hoi thoai 2 nguoi
                if(conversation.getParticipant_list().size() == 2){
                    Intent intent = new Intent(ChatActivity.this, VoiceCallingActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("calleeId", calleeId);
                    bundle.putString("calleeName", calleeName);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    calleeImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    bundle.putByteArray("calleeImage", byteArray);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            }

            case R.id.inforBtn:{ // Nếu người dùng bấm nút infor

                Intent intent = new Intent(ChatActivity.this, SettingUserActivity.class);
                ArrayList<String> list_user = conversation.getParticipantListArray();
                intent.putExtra("IDreceiver", list_user);
                startActivity(intent);
                break;
            }

            case R.id.videoCallBtn:{ // Nếu người dùng bấm nút video call
                if(conversation.getParticipant_list().size() == 2){
                    Intent intent = new Intent(ChatActivity.this, VideoCallingActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("calleeId", calleeId);
                    bundle.putString("calleeName", calleeName);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    calleeImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    bundle.putByteArray("calleeImage", byteArray);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
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

                    //************************************************************************//
                    //Generate ra ID mới dựa trên ID lớn nhất//

                    ArrayList<ConversationModel.Message> list_msg = conversation.getListMessage();
                    String id = (list_msg == null || list_msg.isEmpty()) ? "0" : list_msg.get(list_msg.size() - 1).getId() ;
                    Log.d("test", "onSuccess: " + id);
                    id = String.valueOf(Integer.valueOf(id) + 1);
                    //*********************************End************************************//

                    msg.setId(id);
                    ConversationModel.Message.insertNewMsgToDatabase(msg, id, idConversation);
                    chatBox.setText("");

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
            isStoppedByImage = false;

            try {
                InputStream is = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                String encodeString = Utils.encodeImageForSend(bitmap);

                //********************************************************************************//
                            //Khởi tạo Message mà người dùng vừa gửi//
                ConversationModel.Message msg = new ConversationModel.Message();
                msg.setTimestamp(System.currentTimeMillis());
                msg.setSender(currentUser.getId());
                msg.setText(encodeString);
                msg.setTypeMessage(Constants.KEY_IMAGE);
                //*********************************End********************************************//

                ArrayList<ConversationModel.Message> list_msg = conversation.getListMessage();
                String id = (list_msg == null || list_msg.isEmpty()) ? "0" : list_msg.get(list_msg.size() - 1).getId() ;
                id = String.valueOf(Integer.valueOf(id) + 1);
                //*********************************End************************************//

                msg.setId(id);
                ConversationModel.Message.insertNewMsgToDatabase(msg, id, idConversation);

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
    Boolean isStoppedByImage = false;
    @Override
    public void onGetImageClick(String encodeImage) {
        Intent intent = new Intent(ChatActivity.this, FullImageScreenActivity.class);
        Bitmap bm = Utils.decodeImage(encodeImage);
        String filePath = Utils.tempFileImage(ChatActivity.this, bm, Constants.KEY_IMAGE);
        intent.putExtra(Constants.KEY_IMAGE, filePath);
        startActivity(intent);
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d("FlowTask", "onPause: " + isStoppedByImage);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConversationModel.Message.chatReference.removeEventListener(ConversationModel.Message.childEventListener);
    }

    class fetchData extends Thread  {
        String token;
        String data = "";
        String urlStr;

        public fetchData(String token, String urlStr) {
            this.token = token;
            this.urlStr = urlStr;
        }


        @Override
        public void run() {
            super.run();
            try {
                URL url = new URL(urlStr);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                InputStream inputStream = con.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine())!= null){
                    data += line;
                }
                JSONObject jsonObject = new JSONObject(data);
                this.token = jsonObject.getString("rtcToken");
                myBundle = new Bundle();
                myBundle.putString("token", this.token);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
