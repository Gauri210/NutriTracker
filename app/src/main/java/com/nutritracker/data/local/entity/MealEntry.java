package com.nutritracker.data.local.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "meal_entry_table",
        foreignKeys = {
            @ForeignKey(entity = FoodItem.class, parentColumns = "id", childColumns = "foodId", onDelete = ForeignKey.CASCADE),
            @ForeignKey(entity = ServingUnit.class, parentColumns = "id", childColumns = "unitId", onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("foodId"), @Index("unitId")})
public class MealEntry {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int foodId;
    public int unitId;
    public double quantity;
    public String mealType; // "Breakfast", "Lunch", "Dinner", "Snacks"
    public long dateEpochDay; // To match DailyProgress
}
