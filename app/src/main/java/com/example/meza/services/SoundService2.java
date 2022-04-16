package com.example.meza.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.example.meza.R;

// Incomming Call ringtone
public class SoundService2 extends Service {
    MediaPlayer player;
    public SoundService2() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(getApplicationContext(), R.raw.facebook_messenger_call);
        Log.d("soundService", "soundService2");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        player.setLooping(true);
        player.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("soundService", "destroy");
        player.stop();
        player.release();
        player = null;
    }
}