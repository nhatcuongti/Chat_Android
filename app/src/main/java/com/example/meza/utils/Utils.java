package com.example.meza.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Created by reiko-lhnhat on 3/22/2022.
 */
public class Utils {

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
}
