package com.example.meza.activities;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.meza.utilities.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FCMSend {
    private static String BASE_URL = "https://fcm.googleapis.com/fcm/send";
    private static String SERVER_KEY = "key=AAAAGSRs7KU:APA91bFrXlH9-2AysiiP-yyzfBm9erZVHp5cQAGIZ3EmLI3NdEZZCjjQVTR6cEDDteJsFsh6-mUMji4wdFo8UhMtsn-uOTvcu36fyOOiprvNWol6gbyqw32iCxjmZprWA2EPjDeJXIWe";


    public static void pushNotification(Context context, String token, String title, String message){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(context);

        try{
            JSONObject json = new JSONObject();
            json.put("to", token);
            JSONObject notification = new JSONObject();
            notification.put("title", title);
            notification.put("body", message);
            json.put("notification", notification);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL, json, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("Notification", "FCM" + response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
              @Override
              public Map<String, String> getHeaders() throws AuthFailureError{
                  Map<String, String> params = new HashMap<>();
                  params.put(Constants.REMOTE_MSG_CONTENT_TYPE, "application/json");
                  params.put(Constants.REMOTE_MSG_AUTHORIZATION, SERVER_KEY);
                  return params;
              }
            };

            queue.add(jsonObjectRequest);
        }catch (JSONException e){
            e.printStackTrace();

        }

    }

}
