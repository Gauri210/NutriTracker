package com.nutritracker.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.nutritracker.data.local.entity.DailyProgress;

@Dao
public interface DailyProgressDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertProgress(DailyProgress progress);

    @Update
    void updateProgress(DailyProgress progress);

    @Query("SELECT * FROM daily_progress_table WHERE dateEpochDay = :date")
    LiveData<DailyProgress> getProgressForDate(long date);

    @Query("SELECT * FROM daily_progress_table WHERE dateEpochDay = :date")
    DailyProgress getProgressForDateSync(long date);

    @Query("UPDATE daily_progress_table SET waterGlasses = MAX(0, waterGlasses + :amount) WHERE dateEpochDay = :date")
    void updateWater(long date, int amount);

    @Query("UPDATE daily_progress_table SET caloriesBurned = MAX(0, caloriesBurned + :amount) WHERE dateEpochDay = :date")
    void updateCaloriesBurned(long date, int amount);

    @Query("SELECT * FROM daily_progress_table WHERE dateEpochDay >= :startDate AND dateEpochDay <= :endDate ORDER BY dateEpochDay ASC")
    LiveData<java.util.List<DailyProgress>> getHistoricalProgress(long startDate, long endDate);
}
