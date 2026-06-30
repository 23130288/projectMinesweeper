package com.example.project.utils;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import Model.Session;
import Model.User;

public class UserManager {

    private static final String TAG = "USER_MANAGER";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public interface UserDataCallback {
        void onSuccess(User user);
        void onFailure(Exception e);
    }

    public static void fetchAndSyncSession(ImageView imgAvatar, TextView txtName, UserDataCallback callback) {
        if (mAuth.getCurrentUser() == null) return;

        String uid = mAuth.getCurrentUser().getUid();
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String base64Avatar = documentSnapshot.getString("avatar");

                        if (Session.user == null) {
                            Session.user = new User();
                            Session.user.uid = uid;
                            Session.user.email = mAuth.getCurrentUser().getEmail();
                        }

                        if (name != null) Session.user.name = name;
                        if (base64Avatar != null) Session.user.avatar = base64Avatar;

                        updateUi(imgAvatar, txtName);

                        if (callback != null) callback.onSuccess(Session.user);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user data", e);
                    if (callback != null) callback.onFailure(e);
                });
    }

    public static void updateUi(ImageView imgAvatar, TextView txtName) {
        if (Session.user == null) return;

        if (txtName != null && Session.user.name != null) {
            txtName.setText(Session.user.name);
        }

        if (imgAvatar != null && Session.user.avatar != null && !Session.user.avatar.isEmpty()) {
            Bitmap bitmap = ImageUtils.base64ToBitmap(Session.user.avatar);
            if (bitmap != null) {
                imgAvatar.setImageBitmap(bitmap);
            }
        }
    }
}