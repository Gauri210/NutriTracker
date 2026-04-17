package com.nutritracker.ui.logging;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nutritracker.R;
import com.nutritracker.data.local.entity.FoodItem;
import com.nutritracker.data.local.entity.MealEntry;
import com.nutritracker.data.local.entity.ServingUnit;
import com.nutritracker.databinding.ActivityLogMealBinding;
import com.nutritracker.viewmodel.LogMealViewModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LogMealActivity extends AppCompatActivity {

    private ActivityLogMealBinding binding;
    private LogMealViewModel viewModel;
    private FoodSearchResultAdapter adapter;

    private FoodItem selectedFoodItem;
    private List<ServingUnit> currentUnits = new ArrayList<>();
    private ServingUnit selectedUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogMealBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(LogMealViewModel.class);

        // Setup Back Button
        binding.btnBack.setOnClickListener(v -> finish());

        // Setup Meal Type Spinner
        ArrayAdapter<CharSequence> mealTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.meal_type_array, android.R.layout.simple_spinner_item);
        mealTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerMealType.setAdapter(mealTypeAdapter);

        // Setup RecyclerView
        adapter = new FoodSearchResultAdapter(foodItem -> {
            selectedFoodItem = foodItem;
            binding.layoutServing.setVisibility(View.VISIBLE);
            binding.tvSelectedFood.setText(foodItem.name);
            binding.rvFoodSearch.setVisibility(View.GONE);
            fetchUnitsForFood(foodItem.id);
        });
        binding.rvFoodSearch.setLayoutManager(new LinearLayoutManager(this));
        binding.rvFoodSearch.setAdapter(adapter);

        // Observe search query
        binding.etSearchFood.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 1) {
                    binding.rvFoodSearch.setVisibility(View.VISIBLE);
                    binding.layoutServing.setVisibility(View.GONE);
                    viewModel.search(s.toString());
                } else {
                    binding.rvFoodSearch.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Observe Search Results
        viewModel.getSearchResults().observe(this, foodItems -> {
            Log.d("LogMealActivity", "Search results received: " + (foodItems != null ? foodItems.size() : "null"));
            if (foodItems != null) {
                if (foodItems.isEmpty()) {
                    Log.d("LogMealActivity", "Search results are empty for query: " + binding.etSearchFood.getText().toString());
                }
                adapter.submitList(foodItems);
            }
        });

        // Setup Add to Log Button
        binding.btnAddLog.setOnClickListener(v -> saveMealEntry());
        
        // Listen to quantity changes to update preview
        binding.etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateMacroPreview();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchUnitsForFood(int foodId) {
        viewModel.getUnitsForFood(foodId).observe(this, units -> {
            currentUnits = units;
            // If no custom units exist in DB, add a default Grams unit (fallback)
            if (units == null || units.isEmpty()) {
                currentUnits = new ArrayList<>();
                currentUnits.add(new ServingUnit(foodId, "gram", 1.0));
                currentUnits.add(new ServingUnit(foodId, "100g", 100.0));
            }
            
            List<String> unitNames = new ArrayList<>();
            for (ServingUnit u : currentUnits) {
                unitNames.add(u.unitName);
            }

            ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, unitNames);
            unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.spinnerUnit.setAdapter(unitAdapter);

            binding.spinnerUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedUnit = currentUnits.get(position);
                    updateMacroPreview();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    selectedUnit = null;
                }
            });
            
            // Set initial selection
            if (!currentUnits.isEmpty()) {
                binding.spinnerUnit.setSelection(0);
                selectedUnit = currentUnits.get(0);
                updateMacroPreview();
            }
        });
    }

    private void updateMacroPreview() {
        if (selectedFoodItem == null || selectedUnit == null) return;
        
        String qtyStr = binding.etQuantity.getText().toString();
        if (qtyStr.isEmpty()) qtyStr = "0";
        double qty = Double.parseDouble(qtyStr);

        double totalGrams = selectedUnit.grams * qty;
        int calories = (int) ((selectedFoodItem.caloriesPer100g / 100.0) * totalGrams);
        double protein = (selectedFoodItem.proteinPer100g / 100.0) * totalGrams;
        double carbs = (selectedFoodItem.carbsPer100g / 100.0) * totalGrams;
        double fat = (selectedFoodItem.fatsPer100g / 100.0) * totalGrams;

        binding.tvMacroPreview.setText(String.format("Preview: %d kcal | P: %.1fg | C: %.1fg | F: %.1fg", calories, protein, carbs, fat));
    }

    private void saveMealEntry() {
        if (selectedFoodItem == null || selectedUnit == null) {
            Toast.makeText(this, "Please select a food and unit", Toast.LENGTH_SHORT).show();
            return;
        }

        String qtyStr = binding.etQuantity.getText().toString();
        if (qtyStr.isEmpty()) {
            Toast.makeText(this, "Please enter quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        MealEntry entry = new MealEntry();
        entry.foodId = selectedFoodItem.id;
        // In reality, this relies on unitId being persistent. For fallback logic we can set a dummy or ignore it if we just save grams.
        entry.unitId = selectedUnit.id; 
        entry.quantity = Double.parseDouble(qtyStr);
        entry.mealType = binding.spinnerMealType.getSelectedItem().toString();
        entry.dateEpochDay = LocalDate.now().toEpochDay();

        viewModel.logMeal(entry);

        Toast.makeText(this, "Meal Logged!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
