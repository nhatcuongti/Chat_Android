package com.example.meza.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

/**
 * Created by reiko-lhnhat on 3/22/2022.
 */
public class Utils {
    static public Bitmap encodeBase64StringToBitMapImage(String base64String){
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
