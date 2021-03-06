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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meza.R;
import com.example.meza.model.User;
import com.example.meza.utilities.AlertDialogEx;
import com.example.meza.utilities.Constants;
import com.example.meza.utilities.PreferenceManager;
import com.example.meza.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;

import at.favre.lib.crypto.bcrypt.BCrypt;

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


        //C??i th??ng b??o
        Bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Bell.getText().equals("T???t th??ng b??o")){

                    AlertDialog.Builder alert = new AlertDialog.Builder(SettingActivity.this);
                    alert.setTitle("Th??ng b??o");
                    alert.setMessage("B???n c?? mu???n t???t th??ng b??o c???a c???a ???ng d???ng?");

                    alert.setPositiveButton("C??", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(SettingActivity.this, "???? t???t th??ng b??o ???ng d???ng", Toast.LENGTH_SHORT).show();
                            Bell.setText("B???t th??ng b??o");
                        }
                    });

                    alert.setNegativeButton("Kh??ng", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do nothing
                        }
                    });
                    alert.show();

                }else {
                if (Bell.getText().equals("T???t th??ng b??o")) {
                    Toast.makeText(SettingActivity.this, "???? t???t th??ng b??o ???ng d???ng", Toast.LENGTH_SHORT).show();
                    Bell.setText("B???t th??ng b??o");
                } else {
                    Toast.makeText(SettingActivity.this, "???? b???t th??ng b??o ???ng d???ng", Toast.LENGTH_SHORT).show();
                    Bell.setText("T???t th??ng b??o");
                }
            }
        }});
        //C??i rung
        Vibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Vibrate.getText().equals("T???t rung")){
                    AlertDialog.Builder alert = new AlertDialog.Builder(SettingActivity.this);
                    alert.setTitle("Th??ng b??o");
                    alert.setMessage("B???n c?? mu???n t???t rung kh??ng?");

                    alert.setPositiveButton("C??", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(SettingActivity.this, "???? t???t rung", Toast.LENGTH_SHORT).show();
                            Vibrate.setText("B???t rung");
                        }
                    });

                    alert.setNegativeButton("Kh??ng", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do nothing
                        }
                    });
                    alert.show();


                }else {
                if (Vibrate.getText().equals("T???t rung")) {
                    Toast.makeText(SettingActivity.this, "???? t???t rung", Toast.LENGTH_SHORT).show();
                    Vibrate.setText("B???t rung");
                } else {
                    Toast.makeText(SettingActivity.this, "???? b???t rung", Toast.LENGTH_SHORT).show();
                    Vibrate.setText("T???t rung");
                }
            }
        }});
        //?????i s??? ??i???n tho???i
        Phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingActivity.this, "?????i s??? ??i???n tho???i", Toast.LENGTH_SHORT).show();
            }
        });
        //?????i m???t kh???u
        Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }

        });
        //????ng xu???t
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("Th??ng b??o");
                builder.setMessage("B???n s??? ????ng xu???t kh???i ???ng d???ng?");

                builder.setPositiveButton("C??", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        preferenceManager.clear();
                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                        //x??a token tr??n firebase
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        ref.child(Constants.KEY_COLLECTION_USERS).child(currentUser.getId()).
                                child("token").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });

                        finish();
                    }
                });

                builder.setNegativeButton("Kh??ng", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                });
                AlertDialog alert = builder.create();
                alert.setOnShowListener(arg0 -> {
                    alert.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.primary));
                    alert.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.primary));
                });
                alert.show();

//                Toast.makeText(SettingActivity.this, "????ng xu???t", Toast.LENGTH_SHORT).show();

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

                BCrypt.Result result = null;
                if (oldpw != null) {
                    result = BCrypt.verifyer().verify(pass.toCharArray(), oldpw); // Comparison
                }else if (!result.verified) {
                    Toast.makeText(SettingActivity.this, "M???t kh???u kh??ng ????ng", Toast.LENGTH_SHORT);
                }else if(!newpw.equals(confirmpw)){
                    Toast.makeText(SettingActivity.this, "M???t kh???u kh??ng tr??ng kh???p", Toast.LENGTH_SHORT);
                }else{
                    //m?? h??a m???t kh???u

                    //Update tr??n Firebase
//                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
//                    mDatabase.child(currentUser.id).child(Constants.KEY_PASSWORD).setValue(encodedImage);

                    Toast.makeText(SettingActivity.this, "?????i m???t kh???u th??nh c??ng" + newpw, Toast.LENGTH_SHORT);
                }
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
//            Toast.makeText(SettingActivity.this, "?????i m???t kh???u th??nh c??ng", Toast.LENGTH_SHORT);
//
//
//
//        } else {
//            Toast.makeText(SettingActivity.this, "M???t kh???u kh??ng ????ng", Toast.LENGTH_SHORT);
//        }

    }
    // h??m n??y ?????i ???nh ?????i di???n
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
                            // Update Avater currentUser
                            currentUser.setImage(encodedImage);
                            //Update tr??n Firebase
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


