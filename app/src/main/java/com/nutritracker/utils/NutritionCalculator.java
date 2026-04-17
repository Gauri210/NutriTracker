package com.nutritracker.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class NutritionCalculator {

    public static double calculateBMI(double weightKg, double heightCm) {
        double heightM = heightCm / 100.0;
        return weightKg / (heightM * heightM);
    }

    public static double calculateBMR(String gender, double weightKg, double heightCm, int age) {
        // Mifflin-St Jeor Equation
        // Men: (10 × weight in kg) + (6.25 × height in cm) - (5 × age in years) + 5
        // Women: (10 × weight in kg) + (6.25 × height in cm) - (5 × age in years) - 161
        double base = (10 * weightKg) + (6.25 * heightCm) - (5 * age);
        if ("Male".equalsIgnoreCase(gender)) {
            return base + 5;
        } else {
            return base - 161;
        }
    }

    public static double calculateTDEE(double bmr, String activityLevel) {
        double multiplier = 1.2; // Sedentary
        if (activityLevel != null) {
            String lowerLevel = activityLevel.toLowerCase();
            if (lowerLevel.contains("light")) {
                multiplier = 1.375;
            } else if (lowerLevel.contains("moderate")) {
                multiplier = 1.55;
            } else if (lowerLevel.contains("active")) {
                multiplier = 1.725;
            }
        }
        return bmr * multiplier;
    }

    public static int calculateDailyCalorieTarget(double tdee, double currentWeight, double targetWeight) {
        // Simple deficit/surplus logic
        if (currentWeight > targetWeight) {
            return (int) (tdee - 500); // 500 kcal deficit for weight loss
        } else if (currentWeight < targetWeight) {
            return (int) (tdee + 300); // 300 kcal surplus for weight gain
        } else {
            return (int) tdee; // Maintenance
        }
    }

    public static String estimateGoalDate(double currentWeight, double targetWeight, int dailyDeficit) {
        if (currentWeight == targetWeight) return "Goal reached!";
        // 1 kg of fat is roughly 7700 calories
        double weightDifference = Math.abs(currentWeight - targetWeight);
        double totalCaloriesToBurn = weightDifference * 7700;
        if (dailyDeficit == 0) return "Maintenance mode";
        
        int daysRequired = (int) (totalCaloriesToBurn / Math.abs(dailyDeficit));
        LocalDate goalDate = LocalDate.now().plusDays(daysRequired);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        return goalDate.format(formatter);
    }
}
