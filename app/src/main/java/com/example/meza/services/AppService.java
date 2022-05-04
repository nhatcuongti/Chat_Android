package com.example.meza.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.meza.database.UserDatabase;
import com.example.meza.interfaces.OnGetObjectListener;
import com.example.meza.model.User;
import com.example.meza.utilities.Constants;
import com.example.meza.utilities.PreferenceManager;
import com.google.firebase.database.DataSnapshot;

public class AppService extends Service {


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("TaskRemoved", "onTaskRemoved: " + "OK" );
        UserDatabase userDatabase = UserDatabase.getInstance();
        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        userDatabase.updateStatusUser(preferenceManager.getString(Constants.KEY_USER_ID), 0, new OnGetObjectListener() {
            @Override
            public void onSuccess(Object object) {
                AppService.super.onTaskRemoved(rootIntent);
            }

            @Override
            public void onChange(DataSnapshot snapshot) {

            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
