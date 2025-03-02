package com.sp.mad_project;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.sp.mad_project.placeholder.Add_food_Adapter;
import com.sp.mad_project.placeholder.Food;
import com.sp.mad_project.placeholder.FoodDBHelper;

import org.json.JSONException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Add_food extends AppCompatActivity {

    private static final String TAG = "AddFoodActivity"; // Tag for logging

    Dialog dialog;
    Button dialogAdd, dialogCancel;
    EditText foodNameEditText, caloriesEditText;
    ListView FoodListView;
    Add_food_Adapter add_food_adapter;

    TextView caloriesfood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);
        Log.d(TAG, "onCreate: Activity created");


        // Get Data from FOOD DB
        FoodDBHelper foodDBHelper = FoodDBHelper.instanceOfDatabase(this);

        // Load data from database
        Food.foodArraylist = foodDBHelper.getAllFoods();


        // Get meal type from intent and set it in the TextView
        String mealType = getIntent().getStringExtra("meal_type");
        TextView mealTextView = findViewById(R.id.meal);
        mealTextView.setText(mealType);

        // ListView
        FoodListView = findViewById(R.id.food_history);
        add_food_adapter = new Add_food_Adapter(this, Food.foodArraylist);
        FoodListView.setAdapter(add_food_adapter);

        // Set an ItemClickListener for the ListView
        FoodListView.setOnItemClickListener((parent, view, position, id) -> {
            Food selectedFood = Food.foodArraylist.get(position);
            int foodId = selectedFood.getId();
            if(mealType.equals("Breakfast")){
                foodDBHelper.updateMealType(foodId, FoodDBHelper.COLUMN_BREAKFAST,1);
            }else if (mealType.equals("Lunch")) {
                foodDBHelper.updateMealType(foodId, FoodDBHelper.COLUMN_LUNCH,1);
            } else if (mealType.equals("Dinner")) {
                foodDBHelper.updateMealType(foodId, FoodDBHelper.COLUMN_DINNER,1);
            }
            finish();
        });

        // Initialize ImageButtons
        ImageButton goback = findViewById(R.id.gofoodfragment);
        ImageButton quickAdd = findViewById(R.id.manual_food);
        ImageButton scanBtn = findViewById(R.id.scan_food);

        goback.setOnClickListener(view -> finish());
        quickAdd.setOnClickListener(view -> dialog.show());
        scanBtn.setOnClickListener(view -> {
            Log.d(TAG, "onClick: Scan button clicked");
            scanCode();
        });

       // Initialize the dialog
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.quickaddfood);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.quickadddailog));
        dialog.setCancelable(false);

        // Initialize dialog buttons and fields
        dialogAdd = dialog.findViewById(R.id.Add);
        dialogCancel = dialog.findViewById(R.id.Cancel);
        foodNameEditText = dialog.findViewById(R.id.foodNameDailog);
        caloriesEditText = dialog.findViewById(R.id.CaloriesAmountDailog);

        // Add new food logic
        dialogAdd.setOnClickListener(v -> {
            String foodName = foodNameEditText.getText().toString().trim();
            String caloriesText = caloriesEditText.getText().toString().trim();

            if (foodName.isEmpty() || caloriesText.isEmpty()) {
                Toast.makeText(this, "Please enter both food name and calories.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int calories = Integer.parseInt(caloriesText);
                int id = Food.foodArraylist.size();
                Food newFood = new Food(id, foodName, calories, false, false, false);

                // Add to database
                foodDBHelper.onInsert(newFood);

                // Update in-memory list and notify adapter
                Food.foodArraylist.add(0, newFood); // Add new food to the top
                add_food_adapter.notifyDataSetChanged();

                // Reset fields
                foodNameEditText.setText("");
                caloriesEditText.setText("");
                dialog.dismiss();

                Toast.makeText(this, "Food added: " + foodName + " - " + calories + " calories.", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Log.e(TAG, "Invalid calorie input: " + caloriesText, e);
                Toast.makeText(this, "Invalid calorie input. Please enter a valid number.", Toast.LENGTH_SHORT).show();
            }
        });

        dialogCancel.setOnClickListener(v -> {
            foodNameEditText.setText("");
            caloriesEditText.setText("");
            dialog.dismiss();
        });

    }

    // Barcode scanning function
    private void scanCode() {
        Log.d(TAG, "scanCode: Initializing barcode scanner");
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CapturAct.class); // Use a custom activity if needed
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Code");
        integrator.initiateScan();
    }

    // Handle scan result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String barcode = result.getContents(); // Get scanned barcode
                Log.d(TAG, "onActivityResult: Scanned barcode = " + barcode);
                validateFoodBarcode(barcode);
            } else {
                Log.w(TAG, "onActivityResult: No results found");
                Toast.makeText(this, "No results found.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.w(TAG, "onActivityResult: Result is null");
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void validateFoodBarcode(String barcode) {
        Log.d(TAG, "validateFoodBarcode: Validating barcode = " + barcode);
        OkHttpClient client = new OkHttpClient();
        String apiUrl = "https://world.openfoodfacts.org/api/v2/product/" + barcode + ".json";

        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url(apiUrl)
                        .build();

                Response response = client.newCall(request).execute();
                String responseBody = response.body() != null ? response.body().string() : "";

                if (responseBody.isEmpty()) {
                    runOnUiThread(() -> Toast.makeText(this, "Empty response from server.", Toast.LENGTH_SHORT).show());
                    return;
                }

                JsonObject jsonResponse;
                try {
                    jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                } catch (JsonSyntaxException e) {
                    Log.e(TAG, "Invalid JSON format", e);
                    runOnUiThread(() -> Toast.makeText(this, "Error parsing server response.", Toast.LENGTH_SHORT).show());
                    return;
                }

                if (!jsonResponse.has("status")) {
                    runOnUiThread(() -> Toast.makeText(this, "Unexpected API response.", Toast.LENGTH_SHORT).show());
                    return;
                }

                int status = jsonResponse.get("status").getAsInt();
                Log.d(TAG, "validateFoodBarcode: API response status = " + status);

                if (status == 1) { // Valid food item
                    JsonObject product = jsonResponse.getAsJsonObject("product");
                    String foodName = product.has("product_name") ? product.get("product_name").getAsString() : "Unknown";
                    JsonObject nutriments = product.getAsJsonObject("nutriments");
                    String calories = nutriments.has("energy-kcal") ? nutriments.get("energy-kcal").getAsString() : "N/A";

                    runOnUiThread(() -> addFoodToDatabase(barcode, foodName, calories));
                } else { // Invalid food item
                    Log.w(TAG, "validateFoodBarcode: Invalid food barcode");
                    runOnUiThread(() -> Toast.makeText(this, "Invalid food barcode.", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                Log.e(TAG, "validateFoodBarcode: Error during API call", e);
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }


    private void addFoodToDatabase(String barcode, String foodName, String calories) {
        Log.d(TAG, "addFoodToDatabase: Adding food = " + foodName + ", calories = " + calories);
        FoodDBHelper foodDBHelper = FoodDBHelper.instanceOfDatabase(this);
        int id = Food.foodArraylist.size();

        try {
            int calorieValue = Integer.parseInt(calories);
            Food newFood = new Food(id, foodName, calorieValue, false, false, false);

            // Add to database
            foodDBHelper.onInsert(newFood);

            // Update in-memory list and notify adapter
            Food.foodArraylist.add(0, newFood); // Add new food to the top
            add_food_adapter.notifyDataSetChanged();

            Toast.makeText(this, "Food added: " + foodName + " - " + calories + " calories.", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Log.e(TAG, "addFoodToDatabase: Invalid calorie input from API", e);
            Toast.makeText(this, "Invalid calorie input from API.", Toast.LENGTH_SHORT).show();
        }
    }
}
