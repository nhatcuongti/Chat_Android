package com.example.meza.activities;

import androidx.appcompat.app.AppCompatActivity;
import at.favre.lib.crypto.bcrypt.BCrypt;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.meza.databinding.ActivitySignInBinding;
import com.example.meza.services.SinchService;
import com.example.meza.utilities.Constants;
import com.example.meza.utilities.PreferenceManager;
import com.example.meza.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sinch.android.rtc.SinchError;

public class SignInActivity extends AppCompatActivity implements ServiceConnection, SinchService.SinchClientInitializationListener {


    private static final String TAG = "Login1";
    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;
    SinchService.SinchServiceBinder serviceBinder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
            bindService(new Intent(this, SinchService.class), this, BIND_AUTO_CREATE);
            startActivity(intent);
            finish();
        }
        setListeners();

    }

    private void setListeners() {
        binding.textCreateNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));
        binding.buttonSignIn.setOnClickListener(v -> {
            if (isValidSignInDetails())
                signIn();
        });
    }

    private void signIn() {
        loading(true);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(Constants.KEY_COLLECTION_USERS);
        ref.child(binding.inputPhone.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DataSnapshot ds = task.getResult();
                        String password = binding.inputPassword.getText().toString(); // From user's input
                        String hash = (String) ds.child(Constants.KEY_PASSWORD).getValue(); // From database
                        BCrypt.Result result = null;
                        if (hash != null) {
                             result = BCrypt.verifyer().verify(password.toCharArray(), hash); // Comparison
                        }
                        if (ds.getValue() == null) {
                            loading(false);
                            showToast("Không tìm thấy số điện thoại");
                        } else if (!result.verified) {
                            loading(false);
                            showToast("Mật khẩu không đúng");
                        } else {
                            bindService(new Intent(this, SinchService.class), this, BIND_AUTO_CREATE);


                            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                            preferenceManager.putString(Constants.KEY_USER_ID, binding.inputPhone.getText().toString());
                            preferenceManager.putString(Constants.KEY_FULL_NAME, (String) ds.child(Constants.KEY_FULL_NAME).getValue());
                            preferenceManager.putString(Constants.KEY_IMAGE, (String) ds.child(Constants.KEY_IMAGE).getValue());
                            Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }
                });
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.buttonSignIn.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private Boolean isValidSignInDetails() {
        if (binding.inputPhone.getText().toString().trim().isEmpty()) {
            showToast("Nhập số điện thoại");
            return false;
        } else if (!Patterns.PHONE.matcher(binding.inputPhone.getText().toString()).matches()) {
            showToast("Nhập số điện thoại hợp lệ");
            return false;
        } else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Nhập mật khẩu");
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d("service", "on service connected");
        serviceBinder = (SinchService.SinchServiceBinder)service;
        Utils.serviceBinder = serviceBinder;
        serviceBinder.setClientInitializationListener(this);
        serviceBinder.start(preferenceManager.getString(Constants.KEY_USER_ID));

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    @Override
    public void onStartedSuccessfully() {
        Log.d(TAG, "onStartedSuccessfully");
    }

    @Override
    public void onFailed(SinchError error) {
        Log.e(TAG, "SinchClientInitializationListener error :" + error.getMessage());
    }
}