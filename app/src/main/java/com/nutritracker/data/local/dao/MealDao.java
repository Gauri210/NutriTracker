package com.nutritracker.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.nutritracker.data.local.entity.DailyMacros;
import com.nutritracker.data.local.entity.MealEntry;

import java.util.List;

@Dao
public interface MealDao {
    @Insert
    void insertMealEntry(MealEntry entry);

    @Query("SELECT * FROM meal_entry_table WHERE dateEpochDay = :date")
    LiveData<List<MealEntry>> getMealsForDate(long date);

    @Query("SELECT " +
           "COALESCE(SUM((f.caloriesPer100g / 100.0) * (u.grams * m.quantity)), 0) as totalCalories, " +
           "COALESCE(SUM((f.proteinPer100g / 100.0) * (u.grams * m.quantity)), 0) as totalProtein, " +
           "COALESCE(SUM((f.carbsPer100g / 100.0) * (u.grams * m.quantity)), 0) as totalCarbs, " +
           "COALESCE(SUM((f.fatsPer100g / 100.0) * (u.grams * m.quantity)), 0) as totalFat " +
           "FROM meal_entry_table m " +
           "INNER JOIN food_item_table f ON m.foodId = f.id " +
           "INNER JOIN serving_unit_table u ON m.unitId = u.id " +
           "WHERE m.dateEpochDay = :date")
    LiveData<DailyMacros> getDailyMacros(long date);

    @Query("SELECT m.id as mealId, m.mealType, m.quantity, f.name as foodName, f.caloriesPer100g, u.grams as unitGrams, u.unitName as unitName " +
           "FROM meal_entry_table m " +
           "INNER JOIN food_item_table f ON m.foodId = f.id " +
           "INNER JOIN serving_unit_table u ON m.unitId = u.id " +
           "WHERE m.dateEpochDay = :date " +
           "ORDER BY m.mealType")
    LiveData<List<com.nutritracker.data.local.entity.MealDetailDto>> getMealDetailsForDate(long date);

    @Query("SELECT m.dateEpochDay as dateEpochDay, " +
           "CAST(COALESCE(SUM((f.caloriesPer100g / 100.0) * (u.grams * m.quantity)), 0) AS INTEGER) as totalCalories " +
           "FROM meal_entry_table m " +
           "INNER JOIN food_item_table f ON m.foodId = f.id " +
           "INNER JOIN serving_unit_table u ON m.unitId = u.id " +
           "WHERE m.dateEpochDay >= :startDate AND m.dateEpochDay <= :endDate " +
           "GROUP BY m.dateEpochDay " +
           "ORDER BY m.dateEpochDay ASC")
    LiveData<List<com.nutritracker.data.local.entity.DailyCalories>> getHistoricalCalories(long startDate, long endDate);

    @Query("DELETE FROM meal_entry_table WHERE id = :entryId")
    void deleteMealEntry(int entryId);
}
