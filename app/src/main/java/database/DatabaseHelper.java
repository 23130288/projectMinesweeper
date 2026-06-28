package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "minesweeper.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("""
            CREATE TABLE achievements (
                aid TEXT PRIMARY KEY NOT NULL,
                name TEXT NOT NULL,
                description TEXT
            )
        """);
        insertDefaultAchievements(sqLiteDatabase);
    }
    private void insertDefaultAchievements(SQLiteDatabase db) {
        db.execSQL("""
            INSERT INTO achievements (aid, name, description)
            VALUES
            ('login', 'login', 'login into the game'),
            ('first_win', 'first_win', 'Win 1 game'),
            ('win_10', 'win_10', 'Win 10 games'),
            ('speed_easy', 'speed_easy', 'Finish Easy under 20s'),
            ('speed_medium', 'speed_medium', 'Finish Medium under 60s'),
            ('speed_hard', 'speed_hard', 'Finish Hard under 150s'),
            ('speed_extreme', 'speed_extreme', 'Finish Extreme under 360s')
    """);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
