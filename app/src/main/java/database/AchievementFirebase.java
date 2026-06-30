package database;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import Model.Session;

public class AchievementFirebase {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public void checkAchievements(String uid, String difficulty, int timeSeconds) {
        if (Session.userStats.getGamesWon() >= 1) {
            unlockAchievement(uid, "first_win");
        }
        if (Session.userStats.getGamesWon() >= 10) {
            unlockAchievement(uid, "win_10");
        }
        if (difficulty.equals("easy") && timeSeconds < 20) {
            unlockAchievement(uid, "speed_easy");
        }
        if (difficulty.equals("medium") && timeSeconds < 60) {
            unlockAchievement(uid, "speed_medium");
        }
        if (difficulty.equals("hard") && timeSeconds < 150) {
            unlockAchievement(uid, "speed_hard");
        }
        if (difficulty.equals("extreme")  && timeSeconds < 300) {
            unlockAchievement(uid, "speed_extreme");
        }
    }

    public void unlockAchievement(String uid, String aid) {
        db.collection("users")
                .document(uid)
                .collection("achievements")
                .document(aid)
                .get()
                .addOnSuccessListener(document -> {
                    if (!document.exists()) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("unlockTime", FieldValue.serverTimestamp());
                        db.collection("users")
                                .document(uid)
                                .collection("achievements")
                                .document(aid)
                                .set(data);
                    }
                });
    }
}
