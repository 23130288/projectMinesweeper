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
                aid INTEGER PRIMARY KEY NOT NULL,
                name TEXT NOT NULL,
                description TEXT
            )
        """);

        sqLiteDatabase.execSQL("""
            CREATE TABLE user_achievements (
                uid TEXT NOT NULL,
                aid TEXT NOT NULL,
                unlock_time INTEGER NOT NULL,
                PRIMARY KEY(uid, aid),
                FOREIGN KEY(aid) REFERENCES achievements(aid)
            );
        """);

        sqLiteDatabase.execSQL("""
            CREATE TABLE user_stats (
                uid TEXT PRIMARY KEY,
                games_played INTEGER DEFAULT 0,
                games_won INTEGER DEFAULT 0,
                total_tiles_opened INTEGER DEFAULT 0
            )
        """);
        insertDefaultAchievements(sqLiteDatabase);
    }
    private void insertDefaultAchievements(SQLiteDatabase db) {
        db.execSQL("""
            INSERT INTO achievements (aid, name, description)
            VALUES
            ('first_win', 'first_win', 'Thắng 1 trận'),
            ('win_10', 'win_10', 'Thắng 10 trận'),
            ('speed_easy', 'speed_easy', 'Hoàn thành Easy dưới 20 giây'),
            ('speed_medium', 'speed_medium', 'Hoàn thành Medium dưới 60 giây'),
            ('speed_hard', 'speed_hard', 'Hoàn thành Hard dưới 150 giây'),
            ('speed_extreme', 'speed_extreme', 'Hoàn thành Extreme dưới 300 giây')
    """);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
