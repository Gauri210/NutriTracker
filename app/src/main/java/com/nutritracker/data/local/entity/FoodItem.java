package com.nutritracker.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "food_item_table")
public class FoodItem {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public double caloriesPer100g;
    public double proteinPer100g;
    public double carbsPer100g;
    public double fatsPer100g;
    public double fiberPer100g;

    public FoodItem(String name, double caloriesPer100g, double proteinPer100g, double carbsPer100g, double fatsPer100g, double fiberPer100g) {
        this.name = name;
        this.caloriesPer100g = caloriesPer100g;
        this.proteinPer100g = proteinPer100g;
        this.carbsPer100g = carbsPer100g;
        this.fatsPer100g = fatsPer100g;
        this.fiberPer100g = fiberPer100g;
    }
}
