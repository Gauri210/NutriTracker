package com.nutritracker.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.nutritracker.data.local.entity.DailyMacros;
import com.nutritracker.data.local.entity.DailyProgress;
import com.nutritracker.data.local.entity.MealEntry;
import com.nutritracker.data.local.entity.UserEntity;
import com.nutritracker.data.repository.NutritionRepository;

import java.time.LocalDate;
import java.util.List;

public class DashboardViewModel extends AndroidViewModel {

    private NutritionRepository repository;
    private LiveData<UserEntity> user;
    private LiveData<DailyProgress> todayProgress;
    private LiveData<List<MealEntry>> todayMeals;
    private LiveData<DailyMacros> todayMacros;

    private long todayEpoch;

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        repository = new NutritionRepository(application);
        user = repository.getUser();
        todayEpoch = LocalDate.now().toEpochDay();
        
        todayProgress = repository.getProgressForDate(todayEpoch);
        todayMeals = repository.getMealsForDate(todayEpoch);
        todayMacros = repository.getDailyMacros(todayEpoch);
    }

    public LiveData<UserEntity> getUser() {
        return user;
    }

    public LiveData<DailyProgress> getTodayProgress() {
        return todayProgress;
    }

    public LiveData<List<MealEntry>> getTodayMeals() {
        return todayMeals;
    }

    public LiveData<DailyMacros> getTodayMacros() {
        return todayMacros;
    }

    public void addWaterGlass() {
        repository.updateWater(todayEpoch, 1);
    }
    
    public void removeWaterGlass() {
        repository.updateWater(todayEpoch, -1);
    }

    public void addWorkout() {
        repository.updateCaloriesBurned(todayEpoch, 50);
    }

    public void removeWorkout() {
        repository.updateCaloriesBurned(todayEpoch, -50);
    }

    public LiveData<List<com.nutritracker.data.local.entity.MealDetailDto>> getTodayMealDetails() {
        return repository.getMealDetailsForDate(todayEpoch);
    }

    public LiveData<List<com.nutritracker.data.local.entity.DailyCalories>> getHistoricalCalories() {
        return repository.getHistoricalCalories(todayEpoch - 6, todayEpoch);
    }

    public LiveData<List<DailyProgress>> getHistoricalProgress() {
        return repository.getHistoricalProgress(todayEpoch - 6, todayEpoch);
    }
}
