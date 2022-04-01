package com.example.meza.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.meza.R;
import com.example.meza.databinding.ActivityVerifyOtpBinding;
import com.example.meza.model.User;
import com.example.meza.utilities.Constants;
import com.example.meza.utilities.PreferenceManager;
import com.example.meza.utils.Utilss;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class VerifyOtpActivity extends AppCompatActivity {

    private ActivityVerifyOtpBinding binding;
    private User user;
    private String verificationId;
    private PreferenceManager preferenceManager;
    private final String Tag = "VerifyOtpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        loadReceiverDetails();
        setOtpInputs();
        setListeners();
    }

    private void loadReceiverDetails() {
        user = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textPhoneNumber.setText(String.format(
                "(+84)-%s", user.phone_number
        ));
        verificationId = getIntent().getStringExtra(Constants.KEY_VERIFICATION_ID);
    }

    private void setListeners() {
        binding.buttonVerify.setOnClickListener(v -> {
            if (isValidOtpCode()) {
                verifyOtpAndLogin();
            }
        });
        binding.textResendOtp.setOnClickListener(v -> {
            showToast("Mã OTP mới sẽ được gửi sau 60s kể từ mã cũ");
            resendOtp();
        });
        binding.textRefresh.setOnClickListener(v -> {
            binding.inputCode1.setText("");
            binding.inputCode2.setText("");
            binding.inputCode3.setText("");
            binding.inputCode4.setText("");
            binding.inputCode5.setText("");
            binding.inputCode6.setText("");
            binding.inputCode1.requestFocus();
        });
    }

    private void resendOtp() {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                        .setPhoneNumber("+84" + user.phone_number)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(VerifyOtpActivity.this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                Log.d(Tag, "onVerificationCompleted - " + phoneAuthCredential); // For debugging
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                showToast(e.getMessage());
                            }

                            @Override
                            public void onCodeSent(@NonNull String newVerificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(newVerificationId, forceResendingToken);
                                Log.d(Tag, "onCodeSent - " + newVerificationId); // For debugging
                                verificationId = newVerificationId;
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        Log.d(Tag, "Verification Code: " + verificationId); // For debugging
    }

    private void verifyOtpAndLogin() {
        String inputCode = getOtpInputs();
        if (verificationId != null) {
            Log.d(Tag, "Verification Code: " + verificationId); // For debugging
            loading(true);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            HashMap<String, Object> newUser = new HashMap<>();
            newUser.put(Constants.KEY_FULL_NAME, user.fullname);
            newUser.put(Constants.KEY_PHONE, user.phone_number);
            newUser.put(Constants.KEY_PASSWORD, Utilss.hashPassword(user.password));
            newUser.put(Constants.KEY_IMAGE, user.image);
            newUser.put(Constants.KEY_IS_ACTIVE, 1);
            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, inputCode);
            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DatabaseReference ref = database.getReference();
                            ref.child(Constants.KEY_COLLECTION_USERS).child(user.phone_number)
                                    .setValue(newUser)
                                    .addOnSuccessListener(repository -> {
                                        loading(false);
                                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                                        preferenceManager.putString(Constants.KEY_USER_ID, user.phone_number);
                                        preferenceManager.putString(Constants.KEY_FULL_NAME, user.fullname);
                                        preferenceManager.putString(Constants.KEY_IMAGE, user.image);
                                        Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    })
                                    .addOnFailureListener(exception -> {
                                        loading(false);
                                        showToast(exception.getMessage());
                                    });
                        } else {
                            loading(false);
                            showToast("Mã OTP không hợp lệ");
                        }
                    });
        }
    }



    private void setOtpInputs() {
        binding.inputCode1.addTextChangedListener(new GenericTextWatcher(binding.inputCode1));
        binding.inputCode2.addTextChangedListener(new GenericTextWatcher(binding.inputCode2));
        binding.inputCode3.addTextChangedListener(new GenericTextWatcher(binding.inputCode3));
        binding.inputCode4.addTextChangedListener(new GenericTextWatcher(binding.inputCode4));
        binding.inputCode5.addTextChangedListener(new GenericTextWatcher(binding.inputCode5));
    }

    private String getOtpInputs() {
        return binding.inputCode1.getText().toString() +
                binding.inputCode2.getText().toString() +
                binding.inputCode3.getText().toString() +
                binding.inputCode4.getText().toString() +
                binding.inputCode5.getText().toString() +
                binding.inputCode6.getText().toString();
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonVerify.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.buttonVerify.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private boolean isValidOtpCode() {
        if (binding.inputCode1.getText().toString().trim().isEmpty()
                || binding.inputCode2.getText().toString().trim().isEmpty()
                || binding.inputCode3.getText().toString().trim().isEmpty()
                || binding.inputCode4.getText().toString().trim().isEmpty()
                || binding.inputCode5.getText().toString().trim().isEmpty()
                || binding.inputCode6.getText().toString().trim().isEmpty()) {
            showToast("Mã OTP gồm 6 chữ số");
            return false;
        } else {
            return true;
        }
    }

    class GenericTextWatcher implements TextWatcher {

        private final View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            switch (view.getId()) {
                case R.id.inputCode1:
                    if (!text.isEmpty())
                        binding.inputCode2.requestFocus();
                    break;
                case R.id.inputCode2:
                    if (!text.isEmpty())
                        binding.inputCode3.requestFocus();
                    break;
                case R.id.inputCode3:
                    if (!text.isEmpty())
                        binding.inputCode4.requestFocus();
                    break;
                case R.id.inputCode4:
                    if (!text.isEmpty())
                        binding.inputCode5.requestFocus();
                    break;
                case R.id.inputCode5:
                    if (!text.isEmpty())
                        binding.inputCode6.requestFocus();
                    break;
            }
        }
    }
}
