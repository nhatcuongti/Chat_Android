package com.example.meza.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.meza.R;
import com.example.meza.model.User;
import com.example.meza.utilities.PreferenceManager;
import com.example.meza.utils.Utils;
import com.makeramen.roundedimageview.RoundedImageView;

public class SettingActivity extends Activity {

    PreferenceManager preferenceManager;
    TextView username;
    RoundedImageView userAvatar;
    Button Phone, Password, deleteAccount, Logout, Bell, Vibrate;
    ImageButton backWardBtn;


    User currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        preferenceManager = new PreferenceManager(getApplicationContext());
        //receive current user data from homepage activity
        currentUser = (User) getIntent().getSerializableExtra("currentUser");

        //inflate view
        username = findViewById(R.id.tvUsername);
        userAvatar = findViewById(R.id.imageProfile);

        //set name and profile image (avatar)
        username.setText(currentUser.getFullname());
        if (currentUser.getImage() != null) {
            userAvatar.setImageBitmap(Utils.decodeImage(currentUser.getImage()));
        } else {
            userAvatar.setImageResource(R.drawable.ic_baseline_person_24);
        }

        actionButton();
    }

    private void actionButton() {
        backWardBtn = findViewById(R.id.backwardBtn);
        Bell = (Button) findViewById(R.id.btnNotificationApp);
        Vibrate = (Button) findViewById(R.id.btnVibrate);
        Phone = (Button) findViewById(R.id.btnChangePhone);
        Password = (Button) findViewById(R.id.btnChangePassword);
        deleteAccount = (Button) findViewById(R.id.btnDeleteAccount);
        Logout = (Button) findViewById(R.id.btnLogout);

        Bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Bell.getText().equals("Tắt thông báo")) {
                    Toast.makeText(SettingActivity.this, "Đã tắt thông báo ứng dụng", Toast.LENGTH_SHORT).show();
                    Bell.setText("Bật thông báo");
                } else {
                    Toast.makeText(SettingActivity.this, "Đã bật thông báo ứng dụng", Toast.LENGTH_SHORT).show();
                    Bell.setText("Tắt thông báo");
                }
            }
        });

        Vibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Vibrate.getText().equals("Tắt rung")) {
                    Toast.makeText(SettingActivity.this, "Đã tắt rung", Toast.LENGTH_SHORT).show();
                    Vibrate.setText("Bật rung");
                } else {
                    Toast.makeText(SettingActivity.this, "Đã bật rung", Toast.LENGTH_SHORT).show();
                    Vibrate.setText("Tắt rung");
                }
            }
        });

        Phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingActivity.this, "Đổi số điện thoại", Toast.LENGTH_SHORT).show();
            }
        });

        Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingActivity.this, "Đổi password", Toast.LENGTH_SHORT).show();
            }
        });

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingActivity.this, "Xóa tài khoản", Toast.LENGTH_SHORT).show();
            }
        });

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingActivity.this, "Đăng xuất", Toast.LENGTH_SHORT).show();
                preferenceManager.clear();
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        backWardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
