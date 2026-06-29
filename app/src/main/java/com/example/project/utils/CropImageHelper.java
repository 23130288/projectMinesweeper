package com.example.project.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.yalantis.ucrop.UCrop;
import java.io.File;

public class CropImageHelper {

    public interface CropCallback {
        void onCropSuccess(Bitmap bitmap, String base64String);
    }

    private final AppCompatActivity activity;
    private final CropCallback callback;

    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<Intent> uCropLauncher;

    public CropImageHelper(AppCompatActivity activity, CropCallback callback) {
        this.activity = activity;
        this.callback = callback;
        initLaunchers();
    }

    private void initLaunchers() {
        pickImageLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            startUCrop(imageUri);
                        }
                    }
                }
        );

        uCropLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri resultUri = UCrop.getOutput(result.getData());
                        if (resultUri != null && callback != null) {
                            Bitmap croppedBitmap = ImageUtils.uriToBitmap(activity, resultUri);
                            if (croppedBitmap != null) {
                                String base64String = ImageUtils.bitmapToBase64(croppedBitmap, 85);
                                callback.onCropSuccess(croppedBitmap, base64String);
                            }
                        }
                    }
                }
        );
    }

    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void startUCrop(Uri sourceUri) {
        File destinationFile = new File(activity.getCacheDir(), "cropped_avatar_" + System.currentTimeMillis() + ".jpg");
        Uri destinationUri = Uri.fromFile(destinationFile);

        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(85);
        options.setHideBottomControls(true);
        options.setFreeStyleCropEnabled(false);

        Intent uCropIntent = UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(200, 200)
                .withOptions(options)
                .getIntent(activity);

        uCropLauncher.launch(uCropIntent);
    }
}