package com.nutritracker.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table")
public class UserEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public int age;
    public String gender; // "Male", "Female"
    public double weightKg;
    public double heightCm;
    public String activityLevel; // "Sedentary", "Light", "Moderate", "Active"
    public double targetWeightKg;

    // Calculated fields
    public int dailyCalorieTarget;
    public double bmi;
    public double tdee;
    public String estimatedGoalDate;
}
