package com.nutritracker.ui.onboarding;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.nutritracker.databinding.FragmentOnboardingStep2Binding;
import com.nutritracker.viewmodel.OnboardingViewModel;

public class OnboardingStep2Fragment extends Fragment {

    private FragmentOnboardingStep2Binding binding;
    private OnboardingViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOnboardingStep2Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(OnboardingViewModel.class);

        // Pre-fill
        if (viewModel.weight.getValue() != null) binding.etWeight.setText(String.valueOf(viewModel.weight.getValue()));
        if (viewModel.height.getValue() != null) binding.etHeight.setText(String.valueOf(viewModel.height.getValue()));

        binding.btnContinue.setOnClickListener(v -> {
            String weightStr = binding.etWeight.getText().toString();
            String heightStr = binding.etHeight.getText().toString();

            if (TextUtils.isEmpty(weightStr) || TextUtils.isEmpty(heightStr)) {
                Toast.makeText(requireContext(), "Please enter weight and height", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.weight.setValue(Double.parseDouble(weightStr));
            viewModel.height.setValue(Double.parseDouble(heightStr));

            ((OnboardingActivity) requireActivity()).goToNextStep();
        });

        binding.btnBack.setOnClickListener(v -> {
            ((OnboardingActivity) requireActivity()).goToPreviousStep();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
