package com.example.meza.activities;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meza.R;
import com.example.meza.model.User;
import com.example.meza.utilities.AlertDialogEx;
import com.example.meza.utilities.Constants;
import com.example.meza.utilities.PreferenceManager;
import com.example.meza.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;

//import android.support.v4.media.app.NotificationCompat;

public class SettingActivity extends AppCompatActivity implements AlertDialogEx.DialogListener{

    PreferenceManager preferenceManager;
    TextView username;
    RoundedImageView userAvatar;
    Button Phone, Password, deleteAccount, Logout, Bell, Vibrate;
    EditText oldPassword, newPassword1, newPassword2;
    ImageButton backWardBtn;
    String encodedImage = null;

    TextView textAddImage;
    RoundedImageView imageView;




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

        imageView = findViewById(R.id.imageProfile);


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
                openDialog();
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

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });

    }

    private void openDialog() {
        AlertDialogEx alertDialogEx = new AlertDialogEx();
        alertDialogEx.show(getSupportFragmentManager(), "dialog");


    }

    @Override
    public void applyTexts(String oldpw, String newpw, String confirmpw) {

        currentUser = (User) getIntent().getSerializableExtra("currentUser");

        Log.d("IDcurrentUser", currentUser.getId());

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child(Constants.KEY_COLLECTION_USERS).child(currentUser.getId()).child(Constants.KEY_PASSWORD).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String pass = dataSnapshot.getValue(String.class);

                Log.d("passsss", pass);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("TAG", "Failed to read value.", error.toException());
            }
        });


        Log.d("oldpass", oldpw);
        Log.d("newpass", newpw);
        Log.d("confirm", confirmpw);

//        if(currentUser.getPassword().equals(oldpw)){
//
//            currentUser.setPassword(newpw);
//
//            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
//
//            mDatabase.child(currentUser.id).child(Constants.KEY_PASSWORD).setValue(newpw);
//
//            Intent intent = new Intent(SettingActivity.this, SignInActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//            Toast.makeText(SettingActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT);
//
//
//
//        } else {
//            Toast.makeText(SettingActivity.this, "Mật khẩu không đúng", Toast.LENGTH_SHORT);
//        }

    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {


                            InputStream is = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            imageView.setImageBitmap(bitmap);
//                            binding.imageProfile.setImageBitmap(bitmap);
//                            binding.textAddImage.setVisibility(View.GONE);
                            encodedImage = Utils.encodeImage(bitmap);
                            Log.d("Imageeeeeeeeeeeee", encodedImage);
                            // Update Avater currentUser
                            currentUser.setImage(encodedImage);
                            //Update trên Firebase
                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
                            mDatabase.child(currentUser.id).child(Constants.KEY_IMAGE).setValue(encodedImage);

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
}


