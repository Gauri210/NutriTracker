package com.nutritracker.ui.dashboard;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.nutritracker.R;
import com.nutritracker.data.local.entity.UserEntity;
import com.nutritracker.databinding.FragmentProfileBinding;
import com.nutritracker.utils.NutritionCalculator;
import com.nutritracker.viewmodel.DashboardViewModel;
import com.nutritracker.data.repository.NutritionRepository;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private UserEntity currentUser;
    private NutritionRepository repository;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        DashboardViewModel viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        repository = new NutritionRepository(requireActivity().getApplication());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.activity_level_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerProfileActivity.setAdapter(adapter);

        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null && currentUser == null) {
                currentUser = user;
                binding.etProfileWeight.setText(String.valueOf(user.weightKg));
                binding.etProfileTargetWeight.setText(String.valueOf(user.targetWeightKg));
                
                int spinnerPosition = adapter.getPosition(user.activityLevel);
                if(spinnerPosition >= 0) {
                    binding.spinnerProfileActivity.setSelection(spinnerPosition);
                }
            }
        });

        binding.btnUpdateStats.setOnClickListener(v -> {
            String wStr = Objects.requireNonNull(binding.etProfileWeight.getText()).toString();
            String twStr = Objects.requireNonNull(binding.etProfileTargetWeight.getText()).toString();
            
            if(TextUtils.isEmpty(wStr) || TextUtils.isEmpty(twStr)) {
                Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if(currentUser != null) {
                currentUser.weightKg = Double.parseDouble(wStr);
                currentUser.targetWeightKg = Double.parseDouble(twStr);
                currentUser.activityLevel = binding.spinnerProfileActivity.getSelectedItem().toString();
                
                // Recalculate goals
                currentUser.bmi = NutritionCalculator.calculateBMI(currentUser.weightKg, currentUser.heightCm);
                currentUser.tdee = NutritionCalculator.calculateTDEE(
                        NutritionCalculator.calculateBMR(currentUser.gender, currentUser.weightKg, currentUser.heightCm, currentUser.age),
                        currentUser.activityLevel);

                currentUser.dailyCalorieTarget = NutritionCalculator.calculateDailyCalorieTarget(currentUser.tdee, currentUser.weightKg, currentUser.targetWeightKg);
                
                int deficit = (int) (currentUser.dailyCalorieTarget - currentUser.tdee);
                currentUser.estimatedGoalDate = NutritionCalculator.estimateGoalDate(currentUser.weightKg, currentUser.targetWeightKg, deficit);

                repository.updateUser(currentUser);
                Toast.makeText(requireContext(), "Goals Recalculated & Saved!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
