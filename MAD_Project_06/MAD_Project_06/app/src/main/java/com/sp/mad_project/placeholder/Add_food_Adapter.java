package com.sp.mad_project.placeholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sp.mad_project.R;

import java.util.List;

public class Add_food_Adapter extends ArrayAdapter<Food> {
    public Add_food_Adapter(Context context,List<Food> food){
        super(context,0, food);
    }
    @NonNull
    @Override
    public View getView(int position , @Nullable View convertView, @NonNull ViewGroup parent)
    {
        Food food =getItem(position);
        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.foodcell, parent, false);

        }
        TextView FoodName = convertView.findViewById(R.id.CellFoodName);
        TextView Calories = convertView.findViewById(R.id.CellCalories);

        FoodName.setText(food.getFoodName());
        String caloriesMsg = food.getCalories() + " cal";
        Calories.setText(caloriesMsg);

        return convertView;
    }


}
