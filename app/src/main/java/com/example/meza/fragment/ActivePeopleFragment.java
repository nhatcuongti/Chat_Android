package com.example.meza.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meza.R;
import com.example.meza.adapters.ActiveUserAdapter;
import com.example.meza.model.ConversationModel;
import com.example.meza.model.User;

import java.util.ArrayList;


public class ActivePeopleFragment extends Fragment {

    private ArrayList<User> listActiveUser;
    Context mContext;
    ArrayList<ConversationModel> listRecentConversation;

    View view;
    RecyclerView listActiveUserView;
    ActiveUserAdapter activeUserAdapter;

    public ActivePeopleFragment(Context mContext,ArrayList<User> listActiveUser, ArrayList<ConversationModel> listRecentConversation ) {
        this.listRecentConversation = listRecentConversation;
        this.listActiveUser = listActiveUser;
        this.mContext = mContext;
    }

    public ActiveUserAdapter getActiveUserAdapter() {
        return activeUserAdapter;
    }

    public void setActiveUserAdapter(ActiveUserAdapter activeUserAdapter) {
        this.activeUserAdapter = activeUserAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // active user
        view = inflater.inflate(R.layout.fragment_active_people2, container, false);
        listActiveUserView = view.findViewById(R.id.recycle_list_active_people);
        activeUserAdapter = new ActiveUserAdapter(mContext,listActiveUser,listRecentConversation);
        listActiveUserView.setAdapter(activeUserAdapter);

        return view;
    }
}