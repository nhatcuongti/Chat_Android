package com.example.meza.activities;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meza.R;
import com.example.meza.adapters.ConversationAdapter;
import com.example.meza.model.ConversationModel;

import java.util.ArrayList;


public class ChatActivity extends AppCompatActivity {

    RecyclerView conversationRv;
    ArrayList<ConversationModel.Message> msgList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        conversationRv = findViewById(R.id.conversationListRv);
        //Lấy dữ liệu thô
        initData();
        ConversationModel conversation = new ConversationModel("Hiếu Lê",  R.drawable.hieule, true, msgList);
        //Tạo Adapter và set Adapter cho conversationRV
        conversationRv.setAdapter(new ConversationAdapter(this, conversation));
        // Set LayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        conversationRv.setLayoutManager(linearLayoutManager);

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

        msgList.add(new ConversationModel.Message(true, "Hello"));
        msgList.add(new ConversationModel.Message(false, "Hi ! What's Your Name ?"));
        msgList.add(new ConversationModel.Message(true, "My Name's Hao , How about you ?"));
        msgList.add(new ConversationModel.Message(false, "Hieu. What's your favorite ?"));
        msgList.add(new ConversationModel.Message(true, "My Favorite is Football, I like watching EPL especially match having Man city, Do you know Man City"));
        msgList.add(new ConversationModel.Message(true, "Giờ đang phân vân 3 hãng HP, Lenovo với Thinkpad luôn mà éo biết lấy thằng nào"));

    }

}