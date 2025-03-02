package com.sp.mad_project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

public class BurnCalDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "BurnCal.db";
    private static final int DATABASE_VERSION = 1;

    // Table and column names
    private static final String TABLE_NAME = "burn_calories";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TOTAL_CALORIES = "total_calories";

    public BurnCalDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the table to store total calories
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TOTAL_CALORIES + " INTEGER DEFAULT 0)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the old table if it exists and recreate it
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert or update the total calories value
    public void saveTotalCalories(int totalCalories) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TOTAL_CALORIES, totalCalories);

        // Check if there's already a record
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_ID}, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            // Update existing record
            db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{"1"});
        } else {
            // Insert new record
            db.insert(TABLE_NAME, null, values);
        }
        cursor.close();
        db.close();
    }

    // Retrieve the total calories value
    @SuppressLint("Range")
    public int getTotalCalories() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_TOTAL_CALORIES}, null, null, null, null, null);

        int totalCalories = 0;
        if (cursor != null && cursor.moveToFirst()) {
            totalCalories = cursor.getInt(cursor.getColumnIndex(COLUMN_TOTAL_CALORIES));
            cursor.close();
        }

        db.close();
        return totalCalories;
    }
}
