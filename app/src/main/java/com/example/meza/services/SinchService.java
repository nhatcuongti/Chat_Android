package com.example.meza.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.example.meza.utilities.JWT;
import com.example.meza.utils.Utilss;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;

public class SinchService extends Service implements SinchClientListener, CallClientListener {

    private final String APP_KEY = "b0274bc0-fb51-4fae-b3eb-5d75b673c442";
    private final String APP_SECRET = "aROplhftr0CC4+loLEN7RA==";
    private final String ENVIRONMENT = "ocra.api.sinch.com";
    SinchServiceBinder sinchServiceBinder = new SinchServiceBinder();

    private SinchClient sinchClient = null;
    private String userId= "";
    public SinchClientInitializationListener sinchClientInitializationListener = null;



    public SinchService() {

    }

    private void startInternal(String username) {
        if (sinchClient == null) {
            createClient(username);
        }
        sinchClient.start();
    }

    private void createClient(String username) {
        userId = username;
        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .applicationKey(APP_KEY)
                .environmentHost(ENVIRONMENT)
                .userId(userId)
                .build();
//        sinchClient.setSupportManagedPush(true);
        sinchClient.addSinchClientListener(this);
        Utilss.sinchClient = sinchClient;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return sinchServiceBinder;
//        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onClientStarted(SinchClient p0) {
        sinchClientInitializationListener.onStartedSuccessfully();
    }

    @Override
    public void onClientFailed(SinchClient p0,SinchError p1) {
        sinchClientInitializationListener.onFailed(p1);
    }





    @Override
    public void onLogMessage(int i, String s, String s1) {

    }

    @Override
    public void onPushTokenRegistered() {

    }

    @Override
    public void onPushTokenRegistrationFailed(SinchError sinchError) {

    }

    @Override
    public void onCredentialsRequired(ClientRegistration clientRegistration) {
        clientRegistration.register(JWT.create(APP_KEY, APP_SECRET, userId));
    }

    @Override
    public void onUserRegistered() {

    }

    @Override
    public void onUserRegistrationFailed(SinchError sinchError) {

    }

    @Override
    public void onIncomingCall(CallClient callClient, Call call) {

    }



    public interface SinchClientInitializationListener {
        void onStartedSuccessfully();
        void onFailed(SinchError error);
    }


    public class SinchServiceBinder extends Binder {


        public void start(String username) {
            startInternal(username);
        }
        public Call callUser(String username){
            return sinchClient.getCallClient().callUser(username);
        }

        public void setClientInitializationListener(SinchClientInitializationListener initializationListener) {
            sinchClientInitializationListener = initializationListener;
        }
    }


}