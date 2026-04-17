package com.nutritracker.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "daily_progress_table")
public class DailyProgress {
    @PrimaryKey
    public long dateEpochDay; // e.g., LocalDate.toEpochDay()

    public int caloriesBurned;
    public int waterGlasses; // 1 glass = 250ml
}
