package com.nutritracker.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.nutritracker.data.local.AppDatabase;
import com.nutritracker.data.local.dao.DailyProgressDao;
import com.nutritracker.data.local.dao.FoodDao;
import com.nutritracker.data.local.dao.MealDao;
import com.nutritracker.data.local.dao.UserDao;
import com.nutritracker.data.local.entity.DailyMacros;
import com.nutritracker.data.local.entity.DailyProgress;
import com.nutritracker.data.local.entity.FoodItem;
import com.nutritracker.data.local.entity.MealEntry;
import com.nutritracker.data.local.entity.ServingUnit;
import com.nutritracker.data.local.entity.UserEntity;

import java.util.List;

public class NutritionRepository {

    private UserDao userDao;
    private FoodDao foodDao;
    private MealDao mealDao;
    private DailyProgressDao dailyProgressDao;
    private LiveData<UserEntity> user;

    public NutritionRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
        foodDao = db.foodDao();
        mealDao = db.mealDao();
        dailyProgressDao = db.dailyProgressDao();
        user = userDao.getUser();
    }

    public LiveData<UserEntity> getUser() {
        return user;
    }

    public void insertUser(UserEntity userEntity) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.insertUser(userEntity));
    }

    public void updateUser(UserEntity userEntity) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.updateUser(userEntity));
    }
    
    // Food ops
    public LiveData<List<FoodItem>> searchFood(String query) {
        Log.d("NutritionRepository", "Searching for: '" + query + "'");
        AppDatabase.databaseWriteExecutor.execute(() -> {
            int count = foodDao.getFoodCount();
            Log.d("NutritionRepository", "Current food count in DB: " + count);
        });
        return foodDao.searchFood(query);
    }
    
    public LiveData<List<ServingUnit>> getUnitsForFood(int foodId) {
        return foodDao.getUnitsForFood(foodId);
    }
    
    // Meal ops
    public void insertMealEntry(MealEntry entry) {
        AppDatabase.databaseWriteExecutor.execute(() -> mealDao.insertMealEntry(entry));
    }

    public LiveData<List<MealEntry>> getMealsForDate(long date) {
        return mealDao.getMealsForDate(date);
    }

    public LiveData<DailyMacros> getDailyMacros(long date) {
        return mealDao.getDailyMacros(date);
    }
    
    public void deleteMealEntry(int id) {
        AppDatabase.databaseWriteExecutor.execute(() -> mealDao.deleteMealEntry(id));
    }
    
    // Progress ops
    public LiveData<DailyProgress> getProgressForDate(long date) {
        return dailyProgressDao.getProgressForDate(date);
    }
    
    public LiveData<List<com.nutritracker.data.local.entity.MealDetailDto>> getMealDetailsForDate(long date) {
        return mealDao.getMealDetailsForDate(date);
    }

    public LiveData<List<com.nutritracker.data.local.entity.DailyCalories>> getHistoricalCalories(long startDate, long endDate) {
        return mealDao.getHistoricalCalories(startDate, endDate);
    }

    public LiveData<List<DailyProgress>> getHistoricalProgress(long startDate, long endDate) {
        return dailyProgressDao.getHistoricalProgress(startDate, endDate);
    }
    
    public void updateWater(long date, int amount) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            DailyProgress progress = dailyProgressDao.getProgressForDateSync(date);
            if (progress == null) {
                progress = new DailyProgress();
                progress.dateEpochDay = date;
                progress.waterGlasses = amount > 0 ? amount : 0;
                progress.caloriesBurned = 0;
                dailyProgressDao.insertProgress(progress);
            } else {
                dailyProgressDao.updateWater(date, amount);
            }
        });
    }

    public void updateCaloriesBurned(long date, int amount) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            DailyProgress progress = dailyProgressDao.getProgressForDateSync(date);
            if (progress == null) {
                progress = new DailyProgress();
                progress.dateEpochDay = date;
                progress.waterGlasses = 0;
                progress.caloriesBurned = amount > 0 ? amount : 0;
                dailyProgressDao.insertProgress(progress);
            } else {
                dailyProgressDao.updateCaloriesBurned(date, amount);
            }
        });
    }
}
