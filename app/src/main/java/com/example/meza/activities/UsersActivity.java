package com.example.meza.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.meza.adapters.UsersAdapter;
import com.example.meza.databinding.ActivityUsersBinding;
import com.example.meza.interfaces.UserListener;
import com.example.meza.model.User;
import com.example.meza.utilities.Constants;
import com.example.meza.utilities.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity implements UserListener {

    private ActivityUsersBinding binding;
    private PreferenceManager preferenceManager;
    private final String TAG = "UserActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        getUsers();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
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
                        List<User> users = new ArrayList<>();
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
                        if(users.size() > 0) {
                            UsersAdapter usersAdapter = new UsersAdapter(users, this);
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
    }

    @Override
    public void onRemoveUserClicked(User user) {
        Toast.makeText(getApplicationContext(), "Đã hủy lời mời kết bạn", Toast.LENGTH_SHORT).show();
    }
}