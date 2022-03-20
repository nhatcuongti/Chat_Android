package com.example.meza;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.meza.adapters.ActiveThumnailAdapter;
import com.example.meza.adapters.NameOfConversationAdapter;
import com.example.meza.model.User2;

import java.util.ArrayList;


public class ChatsFragment extends Fragment {
    private ArrayList<User2> listActiveUser;
    private ArrayList<String> listRecentConversation;
    View view;
    RecyclerView listActiveUserView, listRecentConversationView;
    ActiveThumnailAdapter activeThumnailAdapter;
    NameOfConversationAdapter nameOfConversationAdapter;

    public ChatsFragment(ArrayList<User2> listActiveUser) {
        this.listActiveUser = listActiveUser;
    }

    public ChatsFragment(ArrayList<User2> listActiveUser, ArrayList<String> listRecentConversation) {
        this.listActiveUser = listActiveUser;
        this.listRecentConversation = listRecentConversation;
    }

    public ActiveThumnailAdapter getActiveThumnailAdapter() {
        return activeThumnailAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // active user
        view = inflater.inflate(R.layout.fragment_chats, container, false);
        listActiveUserView = view.findViewById(R.id.recycle_list_active_user);
        activeThumnailAdapter = new ActiveThumnailAdapter(listActiveUser);
        listActiveUserView.setAdapter(activeThumnailAdapter);

        //recent conversation
        listRecentConversationView = view.findViewById(R.id.recycle_list_recent_conversation);
        nameOfConversationAdapter = new NameOfConversationAdapter(listRecentConversation);
        listRecentConversationView.setAdapter(nameOfConversationAdapter);

        return view;
    }
}