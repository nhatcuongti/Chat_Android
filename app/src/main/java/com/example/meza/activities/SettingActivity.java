package com.example.meza.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import android.content.Intent;
import android.os.Bundle;
//import android.support.v4.media.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    EditText oldPassword, newPassword1, newPassword2;
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

        oldPassword = (EditText) findViewById(R.id.oldpassword);
        newPassword1 = (EditText) findViewById(R.id.newPassword1);
        newPassword2 = (EditText) findViewById(R.id.newPassword2);

        //Cài thông báo
        Bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Bell.getText().equals("Tắt thông báo")){

                    AlertDialog.Builder alert = new AlertDialog.Builder(SettingActivity.this);
                    alert.setTitle("Thông báo");
                    alert.setMessage("Bạn có muốn tắt thông báo của của ứng dụng?");

                    alert.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(SettingActivity.this, "Đã tắt thông báo ứng dụng", Toast.LENGTH_SHORT).show();
                            Bell.setText("Bật thông báo");
                        }
                    });

                    alert.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do nothing
                        }
                    });
                    alert.show();

                }else {
                if (Bell.getText().equals("Tắt thông báo")) {
                    Toast.makeText(SettingActivity.this, "Đã tắt thông báo ứng dụng", Toast.LENGTH_SHORT).show();
                    Bell.setText("Bật thông báo");
                } else {
                    Toast.makeText(SettingActivity.this, "Đã bật thông báo ứng dụng", Toast.LENGTH_SHORT).show();
                    Bell.setText("Tắt thông báo");
                }
            }
        }});
        //Cài rung
        Vibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Vibrate.getText().equals("Tắt rung")){
                    AlertDialog.Builder alert = new AlertDialog.Builder(SettingActivity.this);
                    alert.setTitle("Thông báo");
                    alert.setMessage("Bạn có muốn tắt rung không?");

                    alert.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(SettingActivity.this, "Đã tắt rung", Toast.LENGTH_SHORT).show();
                            Vibrate.setText("Bật rung");
                        }
                    });

                    alert.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do nothing
                        }
                    });
                    alert.show();


                }else {
                if (Vibrate.getText().equals("Tắt rung")) {
                    Toast.makeText(SettingActivity.this, "Đã tắt rung", Toast.LENGTH_SHORT).show();
                    Vibrate.setText("Bật rung");
                } else {
                    Toast.makeText(SettingActivity.this, "Đã bật rung", Toast.LENGTH_SHORT).show();
                    Vibrate.setText("Tắt rung");
                }
            }
        }});
        //Đổi số điện thoại
        Phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingActivity.this, "Đổi số điện thoại", Toast.LENGTH_SHORT).show();
            }
        });
        //Đổi mật khẩu
        Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder alert = new AlertDialog.Builder(SettingActivity.this);
                LayoutInflater inflater = getLayoutInflater();

                alert.setView(inflater.inflate(R.layout.fragment_change_password, null))
                        .setPositiveButton("Đổi mật khẩu", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Trường hợp đúng mật khẩu cũ
//                                if(oldPassword.getText().toString().equals("abc")){
//                                    //Trường hợp khớp mật khẩu mới
//                                    if(newPassword1.getText().toString().equals("1212")){
//                                        Toast.makeText(SettingActivity.this, "Đổi password", Toast.LENGTH_SHORT).show();
//
//                                    }else {
//                                        Toast.makeText(SettingActivity.this, "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                                else {
//                                    Toast.makeText(SettingActivity.this, "Sai mật khẩu", Toast.LENGTH_SHORT).show();
//                                }
//                                Toast.makeText(SettingActivity.this, "Sai mật khẩu", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .setNegativeButton("Trở lại", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        });

                alert.show();
            }
        });
        //Xóa tài khoản khỏi hệ thống
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(SettingActivity.this);
                alert.setTitle("Thông báo");
                alert.setMessage("Bạn sẽ xóa tài khoản này?");

                alert.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(SettingActivity.this, "Xóa tài khoản thành công", Toast.LENGTH_SHORT).show();
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
        //Đăng xuất
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(SettingActivity.this);
                alert.setTitle("Thông báo");
                alert.setMessage("Bạn sẽ đăng xuất khỏi ứng dụng?");

                alert.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(SettingActivity.this, SignInActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });

                alert.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                });
                alert.show();
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


    }}


