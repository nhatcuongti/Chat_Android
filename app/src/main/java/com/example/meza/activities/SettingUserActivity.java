package com.example.meza.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.meza.R;

public class SettingUserActivity extends Activity {

    Button search, createGroup, notification, deleteHistory, ban;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);

        action();
    }

    private void action() {

        search = (Button)findViewById(R.id.btnSearchText);
        createGroup = (Button)findViewById(R.id.btncreateGroup);
        notification = (Button)findViewById(R.id.btnNotification);
        deleteHistory = (Button) findViewById(R.id.btnDeleteHistory);
        ban = (Button) findViewById(R.id.btnBan);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingUserActivity.this, "Tìm kiếm trong cuộc trò chuyện", Toast.LENGTH_SHORT).show();
            }
        });

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingUserActivity.this, "Tạo group chat", Toast.LENGTH_SHORT).show();
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notification.getText().equals("Tắt thông báo"))
                {
                    AlertDialog.Builder alert = new AlertDialog.Builder(SettingUserActivity.this);
                    alert.setTitle("Thông báo");
                    alert.setMessage("Bạn sẽ tắt thông báo tin nhắn với người này?");

                    alert.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(SettingUserActivity.this, "Đã tắt thông báo cuộc trò chuyện", Toast.LENGTH_SHORT).show();
                            notification.setText("Bật thông báo");
                        }
                    });

                    alert.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do nothing
                        }
                    });
                    alert.show();

                }
                else {
                    Toast.makeText(SettingUserActivity.this, "Đã bật thông báo cuộc trò chuyện", Toast.LENGTH_SHORT).show();
                    notification.setText("Tắt thông báo");
                }


            }
        });

        deleteHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(SettingUserActivity.this);
                alert.setTitle("Thông báo");
                alert.setMessage("Bạn sẽ xóa hết lịch sử cuộc trò chuyện?");

                alert.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(SettingUserActivity.this, "Đã xóa cuộc trò chuyện thành công", Toast.LENGTH_SHORT).show();
                    }
                });

                alert.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                });
                alert.show();
            }
        });

        ban.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(SettingUserActivity.this);
                alert.setTitle("Thông báo");
                alert.setMessage("Bạn sẽ chặn cuộc gọi và tin nhắn của người này?");

                alert.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(SettingUserActivity.this, "Chặn thành công", Toast.LENGTH_SHORT).show();
                    }
                });

                alert.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                });
                alert.show();
            }
        });
    }
}
