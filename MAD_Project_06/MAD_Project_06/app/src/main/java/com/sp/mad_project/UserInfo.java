package com.sp.mad_project;

import android.content.Context;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class UserInfo {
    private String profileImageUrl = "android.resource://com.sp.mad_project/drawable/profile.png";
    private Context context;
    private static String Email = "";
    private static String Username = "";
    private static String Password = "";
    private static String Goal = "";
    private static String ActivityLevel = "";
    private static String Gender = "";
    private static String Country = "";
    private static String PostalCode = "";
    private static String Age = "";
    private static String Height = "";
    private static String Weight = "";
    private static Integer points = 300;
    private static Integer CaloriesBurn= null;
    private static Integer CaloriesGain= null;
    private static Integer Reminder=null;

    public UserInfo() {
    }


    public UserInfo(Context context) {
        this.context = context;
    }


    public void setEmail(String email) {
        Email = email;
    }


    public void setUsername(String username) {
        Username = username;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public void setGoal(String goal) {
        Goal = goal;
    }


    public void setActivityLevel(String activityLevel) {
        ActivityLevel = activityLevel;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public void setAge(String age) {
        Age = age;
    }


    public void setCountry(String country) {
        Country = country;
    }

    public void setPostalCode(String postalCode) {
        PostalCode = postalCode;
    }

    public String getHeight() {
        return Height;
    }

    public void setHeight(String height) {
        Height = height;
    }

    public void setWeight(String weight) {
        Weight = weight;
    }


    public void resetUserData() {
        Email = "";
        Username = "";
        Password = "";
        Goal = "";
        ActivityLevel = "";
        Gender = "";
        Country = "";
        PostalCode = "";
        Age = null;
        Height = null;
        Weight = null;
        points=200;
    }

    public void createNewUserToDB() {
        HashMap<String, Object> userHashMap = new HashMap<>();
        userHashMap.put("email", Email);
        userHashMap.put("password", Password);
        userHashMap.put("username", Username);
        userHashMap.put("gender", Gender);
        userHashMap.put("age", Age);
        userHashMap.put("country", Country);
        userHashMap.put("postalcode", PostalCode);
        userHashMap.put("height", Height);
        userHashMap.put("weight", Weight);
        userHashMap.put("Activitylevel",ActivityLevel);
        userHashMap.put("Goal",Goal);
        userHashMap.put("point",points);
        userHashMap.put("profileImageUrl", profileImageUrl);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fitbro-f45b2-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference emailRef = database.getReference("email");
        String key = emailRef.push().getKey();
        userHashMap.put("key", key);

        if (key != null) {
            emailRef.child(key).setValue(userHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Account Created Successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to create account: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
