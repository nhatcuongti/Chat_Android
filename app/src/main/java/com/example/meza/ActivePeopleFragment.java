package com.example.meza;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.meza.adapters.ActiveThumnailAdapter;
import com.example.meza.adapters.ActiveUserAdapter;
import com.example.meza.adapters.NameOfConversationAdapter;

import java.util.ArrayList;


public class ActivePeopleFragment extends Fragment {

    private ArrayList<String> listActiveUser;
    View view;
    RecyclerView listActiveUserView;
    ActiveUserAdapter activeUserAdapter;

    public ActivePeopleFragment(ArrayList<String> listActiveUser) {
        this.listActiveUser = listActiveUser;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // active user
        view = inflater.inflate(R.layout.fragment_active_people2, container, false);
        listActiveUserView = view.findViewById(R.id.recycle_list_active_people);
        activeUserAdapter = new ActiveUserAdapter(listActiveUser);
        listActiveUserView.setAdapter(activeUserAdapter);

        return view;
    }
}