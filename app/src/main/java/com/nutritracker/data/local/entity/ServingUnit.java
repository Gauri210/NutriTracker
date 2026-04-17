package com.nutritracker.data.local.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "serving_unit_table",
        foreignKeys = @ForeignKey(entity = FoodItem.class,
                parentColumns = "id",
                childColumns = "foodId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("foodId")})
public class ServingUnit {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int foodId;
    public String unitName; // "gram", "cup", "tbsp", "piece"
    public double grams; // Conversion factor

    public ServingUnit(int foodId, String unitName, double grams) {
        this.foodId = foodId;
        this.unitName = unitName;
        this.grams = grams;
    }
}
