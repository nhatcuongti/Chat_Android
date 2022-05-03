package com.example.meza.interfaces;

import com.example.meza.model.User;

public interface UserListener {
    void onAddUserClicked(User user);
    void onRemoveUserClicked(User user);
}
