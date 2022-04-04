package com.example.meza;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.meza.activities.HomePageActivity;
import com.example.meza.adapters.ActiveThumnailAdapter;
import com.example.meza.adapters.NameOfConversationAdapter;
import com.example.meza.model.ConversationModel;
import com.example.meza.model.User;

import java.util.ArrayList;


public class ChatsFragment extends Fragment {
    private ArrayList<User> listActiveUser;
    ArrayList<User> listObjectUserFriend;
    private ArrayList<ConversationModel> listRecentConversation;
    View view;
    RecyclerView listActiveUserView, listRecentConversationView;
    ActiveThumnailAdapter activeThumnailAdapter;
    NameOfConversationAdapter nameOfConversationAdapter;
    Context mcontext;

    public ChatsFragment(){

    }

    public ChatsFragment(Context c,ArrayList<User> listActiveUser) {
        mcontext = c;
        this.listActiveUser = listActiveUser;
    }

    public ChatsFragment(Context c,ArrayList<User> listActiveUser, ArrayList<ConversationModel> listRecentConversation, ArrayList<User> listObjectUserFriend) {
        this.listActiveUser = listActiveUser;
        mcontext = c;
        this.listRecentConversation = listRecentConversation;
        this.listObjectUserFriend = listObjectUserFriend;
    }

    public ActiveThumnailAdapter getActiveThumnailAdapter() {
        return activeThumnailAdapter;
    }

    public NameOfConversationAdapter getNameOfConversationAdapter() {
        return nameOfConversationAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // active user
        view = inflater.inflate(R.layout.fragment_chats, container, false);
        listActiveUserView = view.findViewById(R.id.recycle_list_active_user);
        activeThumnailAdapter = new ActiveThumnailAdapter(mcontext,listActiveUser, listRecentConversation);
        listActiveUserView.setAdapter(activeThumnailAdapter);

        //recent conversation
        listRecentConversationView = view.findViewById(R.id.recycle_list_recent_conversation);
        nameOfConversationAdapter = new NameOfConversationAdapter(mcontext,listRecentConversation, listObjectUserFriend);
        listRecentConversationView.setAdapter(nameOfConversationAdapter);

        return view;
    }
}