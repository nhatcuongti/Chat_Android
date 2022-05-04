package com.example.meza.database;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.meza.interfaces.OnGetObjectListener;
import com.example.meza.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserDatabase {
    private static UserDatabase userDatabase = null;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    String path = "/users";

    private void UserDatabase(){
    }

    public static UserDatabase getInstance(){
        if (userDatabase == null)
            userDatabase = new UserDatabase();

        return userDatabase;
    }

    public void updateStatusUser(String userID, Integer activeNumber, OnGetObjectListener onGetObjectListener){
        String curPath = path + "/" + userID;
        databaseReference = firebaseDatabase.getReference(curPath);
        databaseReference.child("is_active").setValue(activeNumber, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                onGetObjectListener.onSuccess(null);
            }
        });
    }

    public void getUserWithID(String userID,OnGetObjectListener onGetObjectListener){
        String curPath = path + "/" + userID;
        databaseReference = firebaseDatabase.getReference(curPath);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                user.setId(user.getPhone_number());

                onGetObjectListener.onSuccess(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
