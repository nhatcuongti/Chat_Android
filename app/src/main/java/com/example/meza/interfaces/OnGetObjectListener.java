package com.example.meza.interfaces;

import com.google.firebase.database.DataSnapshot;

public interface OnGetObjectListener {
    public void onSuccess(Object object);
    public void onChange(DataSnapshot snapshot);
}
