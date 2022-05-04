package com.example.meza.interfaces;

import com.google.firebase.database.DataSnapshot;

public interface OnGetValueListener {
    public void onSuccess(DataSnapshot snapshot);
    public void onChange(DataSnapshot snapshot);
    public void onRemove(Object object);
}
