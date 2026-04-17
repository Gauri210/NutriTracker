package com.nutritracker.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nutritracker.data.local.entity.UserEntity;
import com.nutritracker.data.repository.NutritionRepository;
import com.nutritracker.utils.NutritionCalculator;

public class OnboardingViewModel extends AndroidViewModel {

    private NutritionRepository repository;

    public MutableLiveData<String> name = new MutableLiveData<>();
    public MutableLiveData<Integer> age = new MutableLiveData<>();
    public MutableLiveData<String> gender = new MutableLiveData<>();
    public MutableLiveData<Double> weight = new MutableLiveData<>();
    public MutableLiveData<Double> height = new MutableLiveData<>();
    public MutableLiveData<String> activityLevel = new MutableLiveData<>();
    public MutableLiveData<Double> targetWeight = new MutableLiveData<>();

    public OnboardingViewModel(@NonNull Application application) {
        super(application);
        repository = new NutritionRepository(application);
    }

    public UserEntity generateDraftUser() {
        UserEntity user = new UserEntity();
        if (name.getValue() != null) user.name = name.getValue();
        if (age.getValue() != null) user.age = age.getValue();
        if (gender.getValue() != null) user.gender = gender.getValue();
        if (weight.getValue() != null) user.weightKg = weight.getValue();
        if (height.getValue() != null) user.heightCm = height.getValue();
        if (activityLevel.getValue() != null) user.activityLevel = activityLevel.getValue();
        if (targetWeight.getValue() != null) user.targetWeightKg = targetWeight.getValue();

        user.bmi = NutritionCalculator.calculateBMI(user.weightKg, user.heightCm);
        user.tdee = NutritionCalculator.calculateTDEE(
                NutritionCalculator.calculateBMR(user.gender, user.weightKg, user.heightCm, user.age),
                user.activityLevel);

        user.dailyCalorieTarget = NutritionCalculator.calculateDailyCalorieTarget(user.tdee, user.weightKg, user.targetWeightKg);
        
        int deficit = (int) (user.dailyCalorieTarget - user.tdee);
        user.estimatedGoalDate = NutritionCalculator.estimateGoalDate(user.weightKg, user.targetWeightKg, deficit);

        return user;
    }

    public void saveUserData(UserEntity draft) {
        repository.insertUser(draft);
    }
    
    public LiveData<UserEntity> getUser() {
        return repository.getUser();
    }
}
