package ca.xiaowei.chen2267127.CRUD;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ca.xiaowei.chen2267127.Model.Task;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "task_database";
    private static final int DATABASE_VERSION = 3;

    private static final String CREATE_TABLE_TASK =
            "CREATE TABLE " + DBContract.TaskEntry.TABLE_NAME + " (" +
                    DBContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DBContract.TaskEntry.COLUMN_USER_ID + " TEXT NOT NULL, " +
                    DBContract.TaskEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                    DBContract.TaskEntry.COLUMN_CATEGORY + " TEXT NOT NULL, " +
                    DBContract.TaskEntry.COLUMN_ADDRESS + " TEXT, " +
                    DBContract.TaskEntry.COLUMN_NOTES + " TEXT," +
                    DBContract.TaskEntry.COLUMN_DATE + " INTEGER, " +
                    DBContract.TaskEntry.COLUMN_TIME + " INTEGER)";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TASK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            // Add the date and time columns to the tasks table
            db.execSQL("ALTER TABLE " + DBContract.TaskEntry.TABLE_NAME + " ADD COLUMN " + DBContract.TaskEntry.COLUMN_DATE + " INTEGER");
            db.execSQL("ALTER TABLE " + DBContract.TaskEntry.TABLE_NAME + " ADD COLUMN " + DBContract.TaskEntry.COLUMN_TIME + " INTEGER");
        }
        // Drop the old table
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.TaskEntry.TABLE_NAME);

        // Recreate the table with the updated schema
        db.execSQL(CREATE_TABLE_TASK);

    }

}