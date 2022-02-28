package com.example.meza;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

public class HomePageActivity extends FragmentActivity {
    ChatsFragment chatsFragment;
    ArrayList<String> listUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        intData();
        chatsFragment = new ChatsFragment(listUser);
        replaceFragment(chatsFragment);
    }
    public void replaceFragment (ChatsFragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_chats, fragment);
        fragmentTransaction.commit();
    }
    public void intData(){
        listUser = new ArrayList<>();
        listUser.add("Bảo Trung");
        listUser.add("Hoàng Nhật");
        listUser.add("Hữu Toàn");
        listUser.add("Việt Hùng");
        listUser.add("Bảo Trung");
        listUser.add("Hoàng Nhật");
        listUser.add("Hữu Toàn");
        listUser.add("Việt Hùng");
        listUser.add("Việt Hùng");
        listUser.add("Bảo Trung");
        listUser.add("Hoàng Nhật");
        listUser.add("Hữu Toàn");
        listUser.add("Việt Hùng");
    }
}