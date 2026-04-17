package com.nutritracker.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nutritracker.data.local.entity.UserEntity;
import com.nutritracker.data.local.entity.MealDetailDto;
import com.nutritracker.data.local.entity.DailyCalories;
import com.nutritracker.data.local.entity.DailyProgress;
import com.nutritracker.databinding.FragmentHomeBinding;
import com.nutritracker.ui.logging.LogMealActivity;
import com.nutritracker.viewmodel.DashboardViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private DashboardViewModel viewModel;
    private UserEntity currentUser;
    private com.nutritracker.data.local.entity.DailyMacros currentMacros;
    private com.nutritracker.data.local.entity.DailyProgress currentProgress;
    private MealAdapter mealAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        binding.btnAddMealFromHome.setOnClickListener(v -> startActivity(new Intent(requireContext(), LogMealActivity.class)));

        binding.btnAddWater.setOnClickListener(v -> viewModel.addWaterGlass());
        binding.btnRemoveWater.setOnClickListener(v -> viewModel.removeWaterGlass());

        binding.cardWorkout.setOnClickListener(v -> startActivity(new Intent(requireContext(), com.nutritracker.ui.logging.WorkoutActivity.class)));

        binding.rvTodayMeals.setLayoutManager(new LinearLayoutManager(requireContext()));
        mealAdapter = new MealAdapter();
        binding.rvTodayMeals.setAdapter(mealAdapter);

        observeData();
    }

    @SuppressLint("DefaultLocale")
    private void observeData() {
        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                currentUser = user;
                syncUI();
            }
        });

        viewModel.getTodayMacros().observe(getViewLifecycleOwner(), macros -> {
            currentMacros = macros;
            syncUI();
        });

        viewModel.getTodayProgress().observe(getViewLifecycleOwner(), progress -> {
            currentProgress = progress;
            if (progress != null) {
                double liters = (progress.waterGlasses * 250) / 1000.0;
                binding.tvWaterLiters.setText(String.format("%.2f L", liters));
                binding.tvWorkoutCals.setText(String.valueOf(progress.caloriesBurned) + " kcal");
            } else {
                binding.tvWaterLiters.setText("0.00 L");
                binding.tvWorkoutCals.setText("0 kcal");
            }
            syncUI();
        });

        viewModel.getTodayMeals().observe(getViewLifecycleOwner(), meals -> {
            if (meals != null) binding.tvMealsCount.setText(String.valueOf(meals.size()));
        });

        viewModel.getTodayMealDetails().observe(getViewLifecycleOwner(), details -> {
            if (details != null && !details.isEmpty()) {
                List<Object> grouped = new ArrayList<>();
                String currentGroup = "";
                for (MealDetailDto d : details) {
                    if (!d.mealType.equals(currentGroup)) {
                        currentGroup = d.mealType;
                        grouped.add(currentGroup);
                    }
                    grouped.add(d);
                }
                mealAdapter.setItems(grouped);
            } else {
                mealAdapter.setItems(new ArrayList<>());
            }
        });

        viewModel.getHistoricalCalories().observe(getViewLifecycleOwner(), dailyCals -> {
            if (dailyCals != null) {
                List<Float> floats = new ArrayList<>();
                for (DailyCalories dc : dailyCals) {
                    floats.add((float) dc.totalCalories);
                }
                binding.graphCalories.setData(floats, "#00BCD4");
            }
        });

        viewModel.getHistoricalProgress().observe(getViewLifecycleOwner(), progressList -> {
            if (progressList != null) {
                List<Float> floats = new ArrayList<>();
                for (DailyProgress dp : progressList) {
                    floats.add((float) dp.caloriesBurned);
                }
                binding.graphWorkouts.setData(floats, "#FF5722");
            }
        });
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void syncUI() {
        if (currentUser == null) return;

        if (currentUser.name != null && !currentUser.name.isEmpty()) {
            binding.tvUserName.setText("Hello " + currentUser.name + "!");
        } else {
            binding.tvUserName.setText("Track Your Health");
        }
        
        int baseTarget = currentUser.dailyCalorieTarget;
        int workoutCals = (currentProgress != null) ? currentProgress.caloriesBurned : 0;
        int newTarget = baseTarget + workoutCals;
        int consumed = (currentMacros != null) ? currentMacros.totalCalories : 0;
        
        binding.tvCalorieTarget.setText("of " + newTarget + " cal");
        binding.tvCaloriesConsumed.setText(String.valueOf(consumed));
        binding.tvCaloriesRemaining.setText(String.valueOf(Math.max(0, newTarget - consumed)));
        
        // Update donut progress: should be empty (0) if no calories consumed, 
        // and increase towards 100 based on target.
        int progress = (newTarget > 0) ? (consumed * 100 / newTarget) : 0;
        binding.progressCalories.setProgress(progress);
        
        int targetProtein = (int) ((newTarget * 0.30) / 4);
        int targetCarbs = (int) ((newTarget * 0.40) / 4);
        int targetFat = (int) ((newTarget * 0.30) / 9);
        
        binding.progressProtein.setMax(targetProtein);
        binding.progressCarbs.setMax(targetCarbs);
        binding.progressFat.setMax(targetFat);
        
        if (currentMacros != null) {
            binding.progressProtein.setProgress((int) currentMacros.totalProtein);
            binding.progressCarbs.setProgress((int) currentMacros.totalCarbs);
            binding.progressFat.setProgress((int) currentMacros.totalFat);
            
            binding.tvProteinRange.setText(String.format("%.0fg / %dg", currentMacros.totalProtein, targetProtein));
            binding.tvCarbsRange.setText(String.format("%.0fg / %dg", currentMacros.totalCarbs, targetCarbs));
            binding.tvFatRange.setText(String.format("%.0fg / %dg", currentMacros.totalFat, targetFat));
        } else {
            binding.progressProtein.setProgress(0);
            binding.progressCarbs.setProgress(0);
            binding.progressFat.setProgress(0);
            
            binding.tvProteinRange.setText(String.format("0g / %dg", targetProtein));
            binding.tvCarbsRange.setText(String.format("0g / %dg", targetCarbs));
            binding.tvFatRange.setText(String.format("0g / %dg", targetFat));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
