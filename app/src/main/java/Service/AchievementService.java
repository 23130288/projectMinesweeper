package Service;

import android.content.Context;

import java.util.List;

import Model.Achievement;
import database.AchievementDao;

public class AchievementService {
    private final AchievementDao ad;

    public AchievementService(Context context) {
        ad = new AchievementDao(context);
    }

    public List<Achievement> getAllAchievements() {
        return ad.getAllAchievements();
    }
}
