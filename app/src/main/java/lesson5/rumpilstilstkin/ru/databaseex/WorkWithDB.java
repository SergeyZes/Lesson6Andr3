package lesson5.rumpilstilstkin.ru.databaseex;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WorkWithDB extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_USERS = "github_users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USER_LOGIN = "login";
    public static final String COLUMN_USER_AVATAR = "avatar_url";
    public static final String DATABASE_NAME = "users.db";


    public WorkWithDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" + COLUMN_USER_ID + " TEXT PRIMARY KEY," +
                COLUMN_USER_LOGIN + " TEXT," + COLUMN_USER_AVATAR + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
