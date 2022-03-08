package com.example.meza.activities;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.meza.ActivePeopleFragment;
import com.example.meza.ChatsFragment;
import com.example.meza.R;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

public class HomePageActivity extends FragmentActivity {
    ChatsFragment chatsFragment;
    TextView fragmentName;
    ActivePeopleFragment activePeopleFragment;
    ArrayList<String> listActiveUser;
    ArrayList<String> listRecentConversation;
    ImageButton chatsBtn, activePeopleBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        intData();
        fragmentName = findViewById(R.id.name_fragment);
        chatsFragment = new ChatsFragment(listActiveUser, listRecentConversation);
        activePeopleFragment = new ActivePeopleFragment(listActiveUser);
        replaceFragment(chatsFragment);
        chatsBtn = findViewById(R.id.chats_Button);
        activePeopleBtn = findViewById(R.id.active_people_Button);

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
    }
    public void replaceFragment (Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_chats, fragment);
        fragmentTransaction.commit();
    }
    public void intData(){
        listActiveUser = new ArrayList<>();
        listActiveUser.add("Linh Giang");
        listActiveUser.add("Thu Nga");
        listActiveUser.add("Quỳnh Hương");
        listActiveUser.add("Hữu Long");
        listActiveUser.add("Ngọc Luân");
        listActiveUser.add("Nhật Anh");
        listActiveUser.add("Bảo Trung");
        listActiveUser.add("Hoàng Nhật");
        listActiveUser.add("Hữu Toàn");
        listActiveUser.add("Việt Hùng");
        listActiveUser.add("Bảo Long");
        listActiveUser.add("Công Lượng");

        listRecentConversation = new ArrayList<>();
        listRecentConversation.add("Linh Giang");
        listRecentConversation.add("Thu Nga");
        listRecentConversation.add("Quỳnh Hương");
        listRecentConversation.add("Hữu Long");
        listRecentConversation.add("Ngọc Luân");
        listRecentConversation.add("Nhật Anh");
        listRecentConversation.add("Bảo Trung");
        listRecentConversation.add("Hoàng Nhật");
        listRecentConversation.add("Hữu Toàn");
        listRecentConversation.add("Việt Hùng");
        listRecentConversation.add("Bảo Long");
        listRecentConversation.add("Công Lượng");
    }
}