package com.sp.mad_project.placeholder;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sp.mad_project.R;
import com.sp.mad_project.TimeDBHelper;
import com.sp.mad_project.UserInfo;
import com.sp.mad_project.placeholder.WeightHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private Dialog updateWeightDialog;
    private WeightHelper weightHelper;
    private BarChart barChart;
    private ImageButton setReminders;
    private TextView setTime;
    private DatabaseReference databaseReference;
    private TextView goalText, food_cal_text;
    private PieChart calorieChart;
    private TimeDBHelper timeDBHelper;

    private int goal_number = 3120; // Example goal

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        weightHelper = new WeightHelper(requireContext());
        barChart = view.findViewById(R.id.weightgraph);
        setTime = view.findViewById(R.id.DashboardTime);
        setReminders = view.findViewById(R.id.setReminder);
        goalText = view.findViewById(R.id.goaltext);
        food_cal_text = view.findViewById(R.id.food_cal_text); // Initialized the TextView here
        calorieChart = view.findViewById(R.id.chart_pie);
        timeDBHelper = new TimeDBHelper(requireContext());
        String savedTime = timeDBHelper.getReminderTime();
        if (savedTime != null) {
            setTime.setText("Reminder: " + savedTime);
        } else {
            setTime.setText("No reminder set");
        }

        goalText.setText("Goal: " + goal_number);

        // Get calories from SharedPreferences and set them
        SharedPreferences prefs = requireContext().getSharedPreferences("FoodPrefs", Context.MODE_PRIVATE);
        int totalCalories = prefs.getInt("totalCalories", 0); // Default to 0 if not found
        food_cal_text.setText("Calories: " + totalCalories);

        // Set OnClickListener for Reminder Button
        setReminders.setOnClickListener(view1 -> showReminderDialog());

        // Automatically update the heading date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        TextView dateTextView = view.findViewById(R.id.Date);
        dateTextView.setText(currentDate);

        // Set Button OnClickListener for Edit Weight Graph
        Button editWeightButton = view.findViewById(R.id.EditWeightGraph);
        editWeightButton.setOnClickListener(v -> showUpdateWeightDialog());

        // Load data from database and display graph
        setupBarChart();

        // Setup for the PieChart with calories
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(totalCalories, "Food Cal"));
        entries.add(new PieEntry(goal_number - totalCalories, "Remaining"));

        PieDataSet pieDataSet = new PieDataSet(entries, "Calories Chart");
        pieDataSet.setColors(Color.parseColor("#FF6347"), Color.parseColor("#32CD32"));
        pieDataSet.setDrawValues(true);

        PieData pieData = new PieData(pieDataSet);
        pieData.setDrawValues(true);

        calorieChart.setData(pieData);
        calorieChart.getDescription().setEnabled(false);
        pieDataSet.setDrawValues(false);

        calorieChart.animateY(1000);
        calorieChart.invalidate();

        return view;
    }

    private void showReminderDialog() {
        // Step 1: Let the user select a time
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                (view, selectedHour, selectedMinute) -> {
                    // Step 2: Ask for reminder name after time is selected
                    askForReminderName(selectedHour, selectedMinute);
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void showUpdateWeightDialog() {
        updateWeightDialog = new Dialog(requireActivity());
        updateWeightDialog.setContentView(R.layout.quickeditweight);
        updateWeightDialog.setCancelable(true);
        updateWeightDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText weightInput = updateWeightDialog.findViewById(R.id.editWeightText);
        Button saveButton = updateWeightDialog.findViewById(R.id.UpdateButtonweightDailog);
        Button cancelButton = updateWeightDialog.findViewById(R.id.CancelButtonweightdailog);

        weightInput.setText("");
        saveButton.setOnClickListener(v -> {
            String weightStr = weightInput.getText().toString();
            if (!weightStr.isEmpty()) {
                float weight = Float.parseFloat(weightStr);
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String currentDate = dateFormat.format(calendar.getTime());

                // Ensure only 10 entries are kept
                limitStoredData();

                // Add new weight to database
                weightHelper.addWeight(weight, currentDate);

                // Refresh the chart
                setupBarChart();
                updateWeightDialog.dismiss();
            } else {
                Toast.makeText(requireContext(), "Please enter a valid weight", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> updateWeightDialog.dismiss());

        updateWeightDialog.show();
    }

    private void limitStoredData() {
        Cursor cursor = weightHelper.getWeightHistory();
        int entryCount = cursor.getCount();

        if (entryCount >= 7) {
            // Delete the oldest record
            if (cursor.moveToFirst()) {
                int oldestId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                weightHelper.deleteWeight(oldestId);
            }
        }
        cursor.close();
    }

    private void askForReminderName(int hour, int minute) {
        // Create an input dialog for the user to enter reminder name
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Enter Reminder Name");

        // Create an EditText for user input
        final EditText input = new EditText(requireContext());
        input.setHint("E.g., Gym, Run");
        builder.setView(input);

        // Set dialog buttons
        builder.setPositiveButton("Next", (dialog, which) -> {
            String reminderName = input.getText().toString().trim();
            if (reminderName.isEmpty()) {
                reminderName = "Workout"; // Default name if empty
            }
            // Step 3: Show confirmation dialog with name and time
            showConfirmationDialog(hour, minute, reminderName);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void showConfirmationDialog(int hour, int minute, String reminderName) {
        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
        String finalReminderText = reminderName + " - " + formattedTime;

        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm Reminder")
                .setMessage("Set reminder '" + reminderName + "' at " + formattedTime + "?")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    try {
                        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
                        intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
                        intent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
                        intent.putExtra(AlarmClock.EXTRA_MESSAGE, reminderName);
                        startActivity(intent);

                        // Save to database and update UI
                        timeDBHelper.saveReminderTime(finalReminderText);
                        setTime.setText(finalReminderText);
                    } catch (Exception e) {
                        showToast("Error setting reminder.");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupBarChart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        List<String> xValues = new ArrayList<>();

        Cursor cursor = weightHelper.getWeightHistory();
        int index = 0;
        float maxWeight = 0f;
        float minWeight = 1000f;

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            float weight = cursor.getFloat(cursor.getColumnIndexOrThrow("weight"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

            entries.add(new BarEntry(index, weight));
            xValues.add(date);

            if (weight > maxWeight) {
                maxWeight = weight;
            }
            if (weight < minWeight) {
                minWeight = weight;
            }
            index++;
        }
        cursor.close();

        if (entries.isEmpty()) {
            return; // No data, don't update chart
        }

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(minWeight - 2);
        yAxis.setAxisMaximum(maxWeight + 2);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(10);
        barChart.getAxisRight().setDrawLabels(false);

        BarDataSet dataSet = new BarDataSet(entries, "Weight Chart");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.invalidate();

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
    }

    private void showToast(String message) {
        if (isAdded() && getActivity() != null) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
