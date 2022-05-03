package com.example.meza.utilities;

import java.util.HashMap;

public class Constants {
    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_FULL_NAME = "fullname";
    public static final String KEY_PHONE = "phone_number";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PREFERENCE_NAME = "mezaPreference";
    public static final String KEY_IS_SIGNED_IN = "is_sign_in";
    public static final String KEY_IS_ACTIVE = "is_active";
    public static final String KEY_LIST_FRIEND = "list_friend";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_TEXT = "text";
    public static final String KEY_USER = "user";
    public static final String KEY_VERIFICATION_ID = "verificationId";
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";

    public static HashMap<String, String> remoteMsgHeaders = null;

    public static HashMap<String, String> getRemoteMsgHeaders(){
        if(remoteMsgHeaders == null){
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(
                    REMOTE_MSG_AUTHORIZATION,
                    "key=AAAAGSRs7KU:APA91bFrXlH9-2AysiiP-yyzfBm9erZVHp5cQAGIZ3EmLI3NdEZZCjjQVTR6cEDDteJsFsh6-mUMji4wdFo8UhMtsn-uOTvcu36fyOOiprvNWol6gbyqw32iCxjmZprWA2EPjDeJXIWe\t\n"
            );
            remoteMsgHeaders.put(
                    REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
            );
        }
        return remoteMsgHeaders;
    }
}