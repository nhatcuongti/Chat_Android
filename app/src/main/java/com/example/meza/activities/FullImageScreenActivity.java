package com.example.meza.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import com.example.meza.R;
import com.example.meza.databinding.ActivityFullImageScreenBinding;
import com.example.meza.utilities.Constants;
import com.example.meza.utils.Utils;

public class FullImageScreenActivity extends AppCompatActivity {

    ActivityFullImageScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFullImageScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        Bitmap bm = intent.getParcelableExtra(Constants.KEY_IMAGE);

        binding.fullImage.setImageBitmap(Utils.resizedBitmap(bm, 600));
        binding.backwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}