package com.sp.mad_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TimeDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "reminder_time.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "ReminderTime";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TIME = "time";

    public TimeDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TIME + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void saveReminderTime(String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME); // Ensure only one time is stored

        ContentValues values = new ContentValues();
        values.put(COLUMN_TIME, time);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public String getReminderTime() {
        SQLiteDatabase db = this.getReadableDatabase();
        String time = null;
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_TIME + " FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            time = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return time;
    }
}
