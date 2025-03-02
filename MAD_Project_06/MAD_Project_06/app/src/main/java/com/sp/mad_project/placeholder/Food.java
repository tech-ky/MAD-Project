package com.sp.mad_project.placeholder;

import java.util.ArrayList;

public class Food {
    private int id;
    private String foodName;
    private int calories;
    private boolean isBreakfast;
    private boolean isLunch;
    private boolean isDinner;
    public static ArrayList<Food> foodArraylist = new ArrayList<>();
    public static ArrayList<Food> breakfastArraylist = new ArrayList<>();
    public static ArrayList<Food> lunchArraylist = new ArrayList<>();
    public static ArrayList<Food> dinnerArraylist = new ArrayList<>();
    public Food(int id, String foodName, int calories, boolean isBreakfast, boolean isLunch, boolean isDinner) {
        this.id = id;
        this.foodName = foodName;
        this.calories = calories;
        this.isBreakfast = isBreakfast;
        this.isLunch = isLunch;
        this.isDinner = isDinner;
    }


    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public boolean isBreakfast() {
        return isBreakfast;
    }

    public void setBreakfast(boolean isBreakfast) {
        this.isBreakfast = isBreakfast;
    }

    public boolean isLunch() {
        return isLunch;
    }

    public void setLunch(boolean isLunch) {
        this.isLunch = isLunch;
    }

    public boolean isDinner() {
        return isDinner;
    }

    public void setDinner(boolean isDinner) {
        this.isDinner = isDinner;
    }
}
