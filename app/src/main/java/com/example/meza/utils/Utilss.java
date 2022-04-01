package com.example.meza.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;
import android.util.Log;

import com.example.meza.services.SinchService;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Created by reiko-lhnhat on 3/22/2022.
 */
public class Utilss {

    public static Call call;
    public static CallClient callClient;
    public static SinchClient sinchClient;
    public static SinchService.SinchServiceBinder serviceBinder;


    // Decode base64 string to set image bitmap
    public static Bitmap decodeImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    // Encode bitmap to base64 string
    public static String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] bytes = baos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static String encodeImageForSend(Bitmap bitmap) {
        int previewHeight = 1000;
//        int previewWidth = (bitmap.getWidth() * previewHeight) / bitmap.getHeight();

        Bitmap previewBitmap = resizedBitmap(bitmap, previewHeight);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    } 

    // Hash password
    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    //Resize bitmap
    public static Bitmap resizedBitmap(Bitmap bm, int newHeight) {
        float aspectRatio = bm.getWidth() /
                (float) bm.getHeight();

        int height = newHeight;
        int width = Math.round(height * aspectRatio);

        Bitmap newBitmap = Bitmap.createScaledBitmap(
                bm, width, height, false);
        return newBitmap;
    }

    //Create temp file
    public static String tempFileImage(Context context, Bitmap bitmap, String name) {

        File outputDir = context.getCacheDir();
        File imageFile = new File(outputDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(context.getClass().getSimpleName(), "Error writing file", e);
        }

        return imageFile.getAbsolutePath();
    }
}
