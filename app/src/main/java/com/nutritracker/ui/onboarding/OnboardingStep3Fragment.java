package com.nutritracker.ui.onboarding;

import android.content.Intent;
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
import com.nutritracker.databinding.FragmentOnboardingStep3Binding;
import com.nutritracker.ui.dashboard.MainActivity;
import com.nutritracker.viewmodel.OnboardingViewModel;

public class OnboardingStep3Fragment extends Fragment {

    private FragmentOnboardingStep3Binding binding;
    private OnboardingViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOnboardingStep3Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(OnboardingViewModel.class);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.activity_level_array));
        binding.spinnerActivityLevel.setAdapter(adapter);

        binding.btnGetStarted.setOnClickListener(v -> {
            String targetWeightStr = binding.etTargetWeight.getText().toString();

            if (TextUtils.isEmpty(targetWeightStr)) {
                Toast.makeText(requireContext(), "Please enter target weight", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.targetWeight.setValue(Double.parseDouble(targetWeightStr));
            viewModel.activityLevel.setValue(binding.spinnerActivityLevel.getText().toString());

            // Delegate to Activity
            ((OnboardingActivity) requireActivity()).showSummaryDialogAndSave();
        });

        binding.btnBack.setOnClickListener(v -> ((OnboardingActivity) requireActivity()).goToPreviousStep());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
