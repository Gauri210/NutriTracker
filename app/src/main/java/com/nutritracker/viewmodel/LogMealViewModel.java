package com.nutritracker.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.nutritracker.data.local.entity.FoodItem;
import com.nutritracker.data.local.entity.MealEntry;
import com.nutritracker.data.local.entity.ServingUnit;
import com.nutritracker.data.repository.NutritionRepository;

import java.util.List;

public class LogMealViewModel extends AndroidViewModel {

    private NutritionRepository repository;
    private MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private LiveData<List<FoodItem>> searchResults;

    public LogMealViewModel(@NonNull Application application) {
        super(application);
        repository = new NutritionRepository(application);
        searchResults = Transformations.switchMap(searchQuery, query -> repository.searchFood(query));
    }

    public void search(String query) {
        searchQuery.setValue(query);
    }

    public LiveData<List<FoodItem>> getSearchResults() {
        return searchResults;
    }

    public LiveData<List<ServingUnit>> getUnitsForFood(int foodId) {
        return repository.getUnitsForFood(foodId);
    }

    public void logMeal(MealEntry entry) {
        repository.insertMealEntry(entry);
    }
}
