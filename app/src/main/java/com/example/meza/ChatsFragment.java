package com.example.meza;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.meza.adapters.ActiveThumnailAdapter;

import java.util.ArrayList;


public class ChatsFragment extends Fragment {
    private ArrayList<String> listActiveUser;
    View view;
    RecyclerView listActiveUserView;
    ActiveThumnailAdapter activeThumnailAdapter;

    public ChatsFragment(ArrayList<String> listActiveUser) {
        this.listActiveUser = listActiveUser;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chats, container, false);
        listActiveUserView = view.findViewById(R.id.recycle_list_active_user);
        activeThumnailAdapter = new ActiveThumnailAdapter(listActiveUser);
        listActiveUserView.setAdapter(activeThumnailAdapter);
        return view;
    }
}