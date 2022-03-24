package com.example.meza.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import com.example.meza.R;
import com.example.meza.databinding.ActivityFullImageScreenBinding;
import com.example.meza.utilities.Constants;
import com.example.meza.utils.Utils;

import java.io.File;

public class FullImageScreenActivity extends AppCompatActivity {

    ActivityFullImageScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFullImageScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String path = intent.getStringExtra(Constants.KEY_IMAGE);

        File file = new File(path);
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        binding.fullImage.setImageBitmap(bitmap);

        binding.backwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}