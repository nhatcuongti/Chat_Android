package com.example.meza.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("/message/1");
        Log.d("hello", "onCreate: "  + binding.btnPush.getText().toString());
        Button btn = findViewById(R.id.btnPush);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                String formattedDateTime = LocalDateTime.now().format(formatter);

                ConversationModel.Message msg = new ConversationModel.Message(binding.idMsg.getText().toString(),
                        binding.sender.getText().toString(),
                        binding.text.getText().toString(),
                        LocalDateTime.now());



                databaseReference.child(msg.getId()).setValue(msg, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Toast.makeText(AddRealtimeDatabase.this, "successful", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("participant_list");
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                test t = snapshot.getValue(test.class);
                ArrayList<String> str = new ArrayList<>(t.getMap().keySet());
                binding.content.setText(str.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

