package com.example.meza.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.meza.databinding.ActivityUsersBinding;
import com.example.meza.interfaces.UserListener;
import com.example.meza.model.User;
import com.example.meza.utilities.Constants;

public class UsersActivity extends AppCompatActivity implements UserListener {

    private ActivityUsersBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    private void setListeners() {
//        binding.imageBack.setOnClickListener();
    }

    private void getUsers() {
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
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
        finish();
    }
}