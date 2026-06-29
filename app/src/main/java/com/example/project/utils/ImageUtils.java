package com.example.project.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ImageUtils {

    public static Bitmap uriToBitmap(Context context, Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            return null;
        }
    }

    public static String bitmapToBase64(Bitmap bitmap, int quality) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception e) {
            return "";
        }
    }

    public static Bitmap base64ToBitmap(String base64String) {
        try {
            byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (Exception e) {
            return null;
        }
    }
}