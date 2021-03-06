package com.example.meza.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meza.databinding.ActivitySignUpBinding;
import com.example.meza.model.User;
import com.example.meza.utilities.Constants;
import com.example.meza.utils.Utils;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private String encodedImage = null;
    private final String Tag = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loading(false);
    }

    private void setListeners() {
        binding.textSignIn.setOnClickListener(v -> onBackPressed());
        binding.buttonSignUp.setOnClickListener(v -> {
            if (isValidSignUpDetails())
                signUp();
        });
        binding.layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void signUp() {
        loading(true);
        showToast("??ang th???c hi???n...");
        User user = new User();
        user.image = encodedImage;
        user.fullname = binding.inputUsername.getText().toString();
        user.phone_number = binding.inputPhone.getText().toString();
        user.password = binding.inputPassword.getText().toString();
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                        .setPhoneNumber("+84" + binding.inputPhone.getText().toString())
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(SignUpActivity.this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                loading(false);
                                Log.d(Tag, "onVerificationCompleted - " + phoneAuthCredential); // For debugging
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                loading(false);
                                Log.d(Tag, "onVerificationFailed - Failed"); // For debugging
                                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                    showToast("Kh??ng th??? g???i m?? OTP");
                                } else if (e instanceof FirebaseTooManyRequestsException) {
                                    showToast("???? g???i OTP qu?? s??? l???n cho ph??p trong ng??y");
                                }

//                                Intent intent = new Intent(getApplicationContext(), VerifyOtpActivity.class);
//                                intent.putExtra(Constants.KEY_USER, user);
//                                startActivity(intent);
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(verificationId, forceResendingToken);
                                Log.d(Tag, "onCodeSent - " + verificationId); // For debugging
                                loading(false);
                                Intent intent = new Intent(getApplicationContext(), VerifyOtpActivity.class);
                                intent.putExtra(Constants.KEY_USER, user);
                                intent.putExtra(Constants.KEY_VERIFICATION_ID, verificationId);
                                startActivity(intent);
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.buttonSignUp.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
    
    // Pick and set profile image activity
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream is = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            binding.imageProfile.setImageBitmap(bitmap);
                            binding.textAddImage.setVisibility(View.GONE);
                            encodedImage = Utils.encodeImage(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private Boolean isValidSignUpDetails() {
        if (binding.inputUsername.getText().toString().trim().isEmpty()) {
            showToast("Nh???p t??n ng?????i d??ng");
            return false;
        } else if (binding.inputPhone.getText().toString().trim().isEmpty()) {
            showToast("Nh???p s??? ??i???n tho???i");
            return false;
        } else if (!Patterns.PHONE.matcher(binding.inputPhone.getText().toString()).matches()) {
            showToast("Nh???p s??? ??i???n tho???i h???p l???");
            return false;
        } else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Nh???p m???t kh???u");
            return false;
        } else if (binding.inputConfirmPassword.getText().toString().trim().isEmpty()) {
            showToast("Nh???p l???i m???t kh???u");
            return false;
        } else if (!binding.inputPassword.getText().toString().equals(binding.inputConfirmPassword.getText().toString())) {
            showToast("M???t kh???u kh??ng tr??ng nhau");
            return false;
        } else {
            return true;
        }
    }
}