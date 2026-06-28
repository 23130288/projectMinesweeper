package database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import Model.Achievement;

public class AchievementDao {
    private final DatabaseHelper helper;

    public AchievementDao(Context context) {
        helper = new DatabaseHelper(context);
    }
    public List<Achievement> getAllAchievements() {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<Achievement> list = new ArrayList<>();
        Cursor cursor = db.rawQuery(
                "SELECT aid, name, description FROM achievements",
                null
        );

        while (cursor.moveToNext()) {
            String aid = cursor.getString(0);
            String name = cursor.getString(1);
            String description = cursor.getString(2);
            list.add(new Achievement(aid, name, description));
        }
        cursor.close();
        db.close();
        return list;
    }
}
