package com.sp.mad_project.placeholder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.sp.mad_project.Add_food;
import com.sp.mad_project.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FoodFragment extends Fragment {
    ListView breakfastlistview, lunchlistview, dinnerlistview;
    Add_food_Adapter add_breakfast_adapter, add_lunch_adapter, add_dinner_adapter;
    Integer totalfoodcalories = 0;
    TextView caloriesfood;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FoodDBHelper foodDBHelper = FoodDBHelper.instanceOfDatabase(getActivity());

        // Check if it's a new day and clear meals if necessary
        resetMealsIfNewDay(foodDBHelper);

        Food.foodArraylist = foodDBHelper.getAllFoods();
        View view = inflater.inflate(R.layout.fragment_food, container, false);

        Button addBreakfastButton = view.findViewById(R.id.add_breakfast);
        Button addLunchButton = view.findViewById(R.id.add_lunch);
        Button addDinnerButton = view.findViewById(R.id.add_dinner);
        caloriesfood = view.findViewById(R.id.total_calories);

        addBreakfastButton.setOnClickListener(v -> handleAddFood("Breakfast"));
        addLunchButton.setOnClickListener(v -> handleAddFood("Lunch"));
        addDinnerButton.setOnClickListener(v -> handleAddFood("Dinner"));

        // Initialize ListViews
        breakfastlistview = view.findViewById(R.id.breakfast_list);
        lunchlistview = view.findViewById(R.id.lunch_list);
        dinnerlistview = view.findViewById(R.id.dinner_list);

        // Set adapters
        add_breakfast_adapter = new Add_food_Adapter(getActivity(), Food.breakfastArraylist);
        add_lunch_adapter = new Add_food_Adapter(getActivity(), Food.lunchArraylist);
        add_dinner_adapter = new Add_food_Adapter(getActivity(), Food.dinnerArraylist);

        breakfastlistview.setAdapter(add_breakfast_adapter);
        lunchlistview.setAdapter(add_lunch_adapter);
        dinnerlistview.setAdapter(add_dinner_adapter);

        SortFoodFromDB();
        return view;
    }

    private void resetMealsIfNewDay(FoodDBHelper foodDBHelper) {
        SharedPreferences prefs = getActivity().getSharedPreferences("FoodPrefs", Context.MODE_PRIVATE);
        String lastSavedDate = prefs.getString("last_date", "");

        String currentDate = getCurrentDate();
        if (!lastSavedDate.equals(currentDate)) {
            // New day detected, clear meal lists
            foodDBHelper.clearMeals();
            prefs.edit().putString("last_date", currentDate).apply();
        }
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    private void SortFoodFromDB() {
        Food.breakfastArraylist.clear();
        Food.lunchArraylist.clear();
        Food.dinnerArraylist.clear();

        for (Food currentFood : Food.foodArraylist) {
            if (currentFood.isBreakfast()) Food.breakfastArraylist.add(currentFood);
            if (currentFood.isLunch()) Food.lunchArraylist.add(currentFood);
            if (currentFood.isDinner()) Food.dinnerArraylist.add(currentFood);
        }

        add_breakfast_adapter.notifyDataSetChanged();
        add_lunch_adapter.notifyDataSetChanged();
        add_dinner_adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        FoodDBHelper foodDBHelper = FoodDBHelper.instanceOfDatabase(getActivity());
        Food.foodArraylist = foodDBHelper.getAllFoods();
        SortFoodFromDB();
        calculateTotalCalories();
    }

    private void handleAddFood(String mealType) {
        Intent intent = new Intent(getActivity(), Add_food.class);
        intent.putExtra("meal_type", mealType);
        startActivity(intent);
    }

    private void calculateTotalCalories() {
        totalfoodcalories = 0;

        for (Food food : Food.breakfastArraylist) totalfoodcalories += food.getCalories();
        for (Food food : Food.lunchArraylist) totalfoodcalories += food.getCalories();
        for (Food food : Food.dinnerArraylist) totalfoodcalories += food.getCalories();

        SharedPreferences prefs = getActivity().getSharedPreferences("FoodPrefs", Context.MODE_PRIVATE);
        prefs.edit().putInt("totalCalories", totalfoodcalories).apply();
    }
}
