package com.example.meza.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.meza.adapters.UsersAdapter;
import com.example.meza.databinding.ActivityUsersBinding;
import com.example.meza.interfaces.UserListener;
import com.example.meza.model.User;
import com.example.meza.utilities.Constants;
import com.example.meza.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UsersActivity extends AppCompatActivity implements UserListener {

    private ActivityUsersBinding binding;
    private PreferenceManager preferenceManager;
    private List<User> users;
    private UsersAdapter usersAdapter;
    private final String TAG = "UserActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        users = new ArrayList<>();
        setListeners();
        getUsers();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                usersAdapter.filter(s);
                return false;
            }
        });
    }

    private void getUsers() {
        loading(true);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        DataSnapshot ds = task.getResult();
                        for (DataSnapshot snapshot : ds.getChildren()) {
                            Log.d(TAG, String.valueOf(snapshot.getKey()));
                            if (currentUserId.equals((String) snapshot.getKey()))
                                continue;
                            User user = new User();
                            user.fullname = (String) snapshot.child(Constants.KEY_FULL_NAME).getValue();
                            user.phone_number = (String) snapshot.child(Constants.KEY_PHONE).getValue();
                            user.image = (String) snapshot.child(Constants.KEY_IMAGE).getValue();
                            user.id = (String) snapshot.getKey();
                            users.add(user);
                        }
                        if (users.size() > 0) {
                            usersAdapter = new UsersAdapter(users, this);
                            binding.usersRecyclerView.setAdapter(usersAdapter);
                            binding.usersRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            showErrMessage();
                        }
                    } else {
                        showErrMessage();
                    }
                });
    }

    private void showErrMessage() {
        binding.textErrorMessage.setText(String.format("%s", "Không tìm thấy người dùng"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onAddUserClicked(User user) {
//        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
//        intent.putExtra(Constants.KEY_USER, user);
//        startActivity(intent);
//        finish();
        Toast.makeText(getApplicationContext(), "Đã gửi lời mời kết bạn", Toast.LENGTH_SHORT).show();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(Constants.KEY_COLLECTION_USERS);
        ref.child(preferenceManager.getString(Constants.KEY_USER_ID))
                .child(Constants.KEY_LIST_FRIEND)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DataSnapshot ds = task.getResult();
                        HashMap<String, Object> newUser = new HashMap<>();

                        if (ds.getValue() != null) {
                            int numberOfFriends = 0;
                            for (DataSnapshot snapshot : ds.getChildren()) {
                                newUser.put(String.valueOf(numberOfFriends), String.valueOf(snapshot.getValue()));
                                numberOfFriends++;
                            }
                            newUser.put(String.valueOf(numberOfFriends), user.phone_number);
                            addNewFriend(newUser);
                        } else {
                            newUser.put("0", user.phone_number);
                            addNewFriend(newUser);
                        }
                    }
                });
    }

    private void addNewFriend(HashMap<String, Object> list) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(Constants.KEY_COLLECTION_USERS);
        ref.child(preferenceManager.getString(Constants.KEY_USER_ID))
                .child(Constants.KEY_LIST_FRIEND)
                .setValue(list)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        // ...
                    }
                });
    }

    @Override
    public void onRemoveUserClicked(User user) {
        Toast.makeText(getApplicationContext(), "Đã hủy lời mời kết bạn", Toast.LENGTH_SHORT).show();
    }
}