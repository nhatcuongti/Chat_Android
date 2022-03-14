package com.example.meza.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.meza.R;

public class SettingActivity extends Activity {

    Button Phone, Password, deleteAccount, Logout, Bell, Vibrate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        actionButton();
    }

    private void actionButton() {
        Bell = (Button) findViewById(R.id.btnNotificationApp);
        Vibrate = (Button) findViewById(R.id.btnVibrate);
        Phone = (Button) findViewById(R.id.btnChangePhone);
        Password = (Button)findViewById(R.id.btnChangePassword);
        deleteAccount = (Button)findViewById(R.id.btnDeleteAccount);
        Logout = (Button)findViewById(R.id.btnLogout);

        Bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Bell.getText().equals("Tắt thông báo")){
                    Toast.makeText(SettingActivity.this, "Đã tắt thông báo ứng dụng", Toast.LENGTH_SHORT).show();
                    Bell.setText("Bật thông báo");
                }else {
                    Toast.makeText(SettingActivity.this, "Đã bật thông báo ứng dụng", Toast.LENGTH_SHORT).show();
                    Bell.setText("Tắt thông báo");
                }
            }
        });

        Vibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Vibrate.getText().equals("Tắt rung")){
                    Toast.makeText(SettingActivity.this, "Đã tắt rung", Toast.LENGTH_SHORT).show();
                    Vibrate.setText("Bật rung");
                }else {
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
            }
        });
    }

}
