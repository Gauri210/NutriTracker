package com.nutritracker.ui.logging;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nutritracker.data.repository.NutritionRepository;
import com.nutritracker.databinding.ActivityWorkoutBinding;

import java.time.LocalDate;

public class WorkoutActivity extends AppCompatActivity {

    private ActivityWorkoutBinding binding;
    private NutritionRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWorkoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new NutritionRepository(getApplication());

        // Setup Back Button
        binding.layoutBack.setOnClickListener(v -> finish());
        binding.btnBack.setOnClickListener(v -> finish());

        // Setup Spinner
        String[] workouts = new String[]{"Walking", "Running", "Cycling", "Swimming"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, workouts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerWorkoutType.setAdapter(adapter);

        String[] intensities = new String[]{"Low", "Medium", "High"};
        ArrayAdapter<String> intAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, intensities);
        intAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerIntensity.setAdapter(intAdapter);

        binding.btnAddWorkout.setOnClickListener(v -> handleSave());
    }

    private void handleSave() {
        String directStr = binding.etDirectCalories.getText() != null ? binding.etDirectCalories.getText().toString() : "";
        int caloriesToBurn;

        if (!TextUtils.isEmpty(directStr)) {
            caloriesToBurn = Integer.parseInt(directStr);
        } else {
            String durationStr = binding.etDuration.getText() != null ? binding.etDuration.getText().toString() : "";
            if (TextUtils.isEmpty(durationStr)) {
                Toast.makeText(this, "Please enter duration or direct calories", Toast.LENGTH_SHORT).show();
                return;
            }
            int duration = Integer.parseInt(durationStr);
            int selectedPosition = binding.spinnerWorkoutType.getSelectedItemPosition();
            int intPosition = binding.spinnerIntensity.getSelectedItemPosition();

            int calsPerMin = calculateCaloriesPerMinute(selectedPosition, intPosition);
            caloriesToBurn = duration * calsPerMin;
        }

        if (caloriesToBurn > 0) {
            long todayEpoch = LocalDate.now().toEpochDay();
            repository.updateCaloriesBurned(todayEpoch, caloriesToBurn);
            Toast.makeText(this, "Logged " + caloriesToBurn + " kcal burned!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private int calculateCaloriesPerMinute(int selectedPosition, int intPosition) {
        // Basic MET approximations
        int calsPerMin = 5;
        if (selectedPosition == 0) {
            calsPerMin = (intPosition == 0) ? 3 : (intPosition == 1) ? 5 : 7;
        } else if (selectedPosition == 1) {
            calsPerMin = (intPosition == 0) ? 8 : (intPosition == 1) ? 10 : 13;
        } else if (selectedPosition == 2) {
            calsPerMin = (intPosition == 0) ? 6 : (intPosition == 1) ? 8 : 11;
        } else if (selectedPosition == 3) {
            calsPerMin = (intPosition == 0) ? 7 : (intPosition == 1) ? 9 : 12;
        }
        return calsPerMin;
    }
}
