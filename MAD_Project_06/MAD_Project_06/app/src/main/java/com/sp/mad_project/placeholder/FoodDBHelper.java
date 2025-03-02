package com.sp.mad_project.placeholder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class FoodDBHelper extends SQLiteOpenHelper {

    // Database constants
    private static final String DATABASE_NAME = "SQLiteFitbro";
    private static final int DATABASE_VERSION = 1;

    // Table and column constants
    public static final String TABLE_FOOD = "food_items";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FOODNAME = "name";
    public static final String COLUMN_CALORIES = "calories";
    public static final String COLUMN_BREAKFAST = "breakfast";  // 0 or 1
    public static final String COLUMN_LUNCH = "lunch";          // 0 or 1
    public static final String COLUMN_DINNER = "dinner";        // 0 or 1

    private static FoodDBHelper instance;

    public FoodDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized FoodDBHelper instanceOfDatabase(Context context) {
        if (instance == null)
            instance = new FoodDBHelper(context);
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Execute SQL to create the table
        db.execSQL("CREATE TABLE " + TABLE_FOOD + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FOODNAME + " TEXT NOT NULL, " +
                COLUMN_CALORIES + " INTEGER NOT NULL, " +
                COLUMN_BREAKFAST + " INTEGER NOT NULL, " +  // Store 0 (false) or 1 (true)
                COLUMN_LUNCH + " INTEGER NOT NULL, " +
                COLUMN_DINNER + " INTEGER NOT NULL" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrade if needed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD);
        onCreate(db);
    }

    public void onInsert(Food food) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_FOODNAME, food.getFoodName());
        contentValues.put(COLUMN_CALORIES, food.getCalories());
        contentValues.put(COLUMN_BREAKFAST, food.isBreakfast() ? 1 : 0); // Store 1 for breakfast, 0 for other types
        contentValues.put(COLUMN_LUNCH, food.isLunch() ? 1 : 0); // Store 1 for lunch, 0 for other types
        contentValues.put(COLUMN_DINNER, food.isDinner() ? 1 : 0); // Store 1 for dinner, 0 for other types

        sqLiteDatabase.insert(TABLE_FOOD, null, contentValues);
    }

    public ArrayList<Food> getAllFoods() {
        ArrayList<Food> foodList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FOOD, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOODNAME));
                int calories = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CALORIES));
                boolean isBreakfast = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BREAKFAST)) == 1;
                boolean isLunch = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LUNCH)) == 1;
                boolean isDinner = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DINNER)) == 1;

                foodList.add(new Food(id, name, calories, isBreakfast, isLunch, isDinner));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return foodList;
    }

    public Food getFoodById(int foodId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_FOOD,         // Table name
                null,               // Select all columns
                COLUMN_ID + "=?",   // WHERE clause
                new String[]{String.valueOf(foodId)}, // Where clause value
                null,               // Group by
                null,               // Having
                null                // Order by
        );

        if (cursor != null) {
            cursor.moveToFirst();
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOODNAME));
            int calories = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CALORIES));
            boolean isBreakfast = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BREAKFAST)) == 0;
            boolean isLunch = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LUNCH)) == 0;
            boolean isDinner = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DINNER)) == 0;

            cursor.close();
            return new Food(id, name, calories, isBreakfast, isLunch, isDinner); // Return the food item object
        } else {
            return null; // Return null
        }
    }
    public void updateMealType(int foodId, String mealTypeColumn, int update) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Set the specified meal type to true
        ContentValues updateValues = new ContentValues();
        updateValues.put(mealTypeColumn, update);
        db.update("food_items", updateValues, "id = ?", new String[]{String.valueOf(foodId)});

        db.close();
    }
    public void clearMeals() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BREAKFAST, 0);
        values.put(COLUMN_LUNCH, 0);
        values.put(COLUMN_DINNER, 0);

        db.update(TABLE_FOOD, values, null, null);
        db.close();
    }
    public void deleteAllFoods() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FOOD, null, null); // Deletes all records
        db.close();
    }


}