package com.example.meza.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.meza.interfaces.OnGetValueListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    public String fullname, image, phone_number, password, id;
    public ArrayList<String> list_friend = new ArrayList<>();
    public boolean isActive;

    public User() {

    }

    public User(String username, String image, String phone, String password, String id, boolean isActive) {
        this.fullname = username;
        this.image = image;
        this.phone_number = phone;
        this.password = password;
        this.id = phone_number;
        this.isActive = isActive;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + fullname + '\'' +
                ", image='" + image + '\'' +
                ", phone='" + phone_number + '\'' +
                ", password='" + password + '\'' +
                ", id='" + id + '\'' +
                ", list_friend=" + list_friend +
                ", isActive=" + isActive +
                '}';
    }

    private static ArrayList<User> list_user = new ArrayList<>();

    public static void initData(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Xử lý khi có thay đổi database
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                list_user.add(user);
                Log.d("abcd", "onChildAdded: " + user.toString());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);

                if (list_user == null || list_user.isEmpty())
                    return;

                for (int i = 0; i < list_user.size(); i++)
                    if (user.getPhone_number().equals(list_user.get(i).getPhone_number())){
                        list_user.set(i, user);
                        break;
                    }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if (list_user == null || list_user.isEmpty())
                    return;

                for (int i = 0; i < list_user.size(); i++)
                    if (user.getPhone_number().equals(list_user.get(i).getPhone_number())){
                        list_user.remove(i);
                        break;
                    }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void getUserWithID(String id, OnGetValueListener onGetValueListener){
        String path = "/users/" + id;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                onGetValueListener.onSuccess(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
