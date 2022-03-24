package com.example.meza.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.meza.R;
import com.example.meza.databinding.ActivityAddRealtimeDatabaseBinding;
import com.example.meza.interfaces.OnGetValueListener;
import com.example.meza.model.ConversationModel;
import com.example.meza.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.Inflater;

public class AddRealtimeDatabase extends AppCompatActivity {

    ActivityAddRealtimeDatabaseBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRealtimeDatabaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



    }
}

class test{
    Map<String, String> map;

    public void test(){

    }

    public Map<String, String> getMap(){
        return map;
    }
}

