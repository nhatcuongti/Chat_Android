package com.example.meza;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

public class HomePageActivity extends FragmentActivity {
    ChatsFragment chatsFragment;
    ArrayList<String> listActiveUser;
    ArrayList<String> listRecentConversation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        intData();
        chatsFragment = new ChatsFragment(listActiveUser, listRecentConversation);
        replaceFragment(chatsFragment);
    }
    public void replaceFragment (ChatsFragment fragment){
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