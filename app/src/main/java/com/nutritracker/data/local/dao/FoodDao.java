package com.nutritracker.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.nutritracker.data.local.entity.FoodItem;
import com.nutritracker.data.local.entity.ServingUnit;

import java.util.List;

@Dao
public interface FoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFoodItems(List<FoodItem> items);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertServingUnits(List<ServingUnit> units);

    @Query("SELECT * FROM food_item_table WHERE name LIKE '%' || :searchQuery || '%' COLLATE NOCASE")
    LiveData<List<FoodItem>> searchFood(String searchQuery);

    @Query("SELECT * FROM serving_unit_table WHERE foodId = :foodId")
    LiveData<List<ServingUnit>> getUnitsForFood(int foodId);

    @Query("SELECT COUNT(id) FROM food_item_table")
    int getFoodCount();
}
