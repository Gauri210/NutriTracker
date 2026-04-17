package com.nutritracker.ui.onboarding;

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
import com.nutritracker.databinding.FragmentOnboardingStep1Binding;
import com.nutritracker.viewmodel.OnboardingViewModel;

public class OnboardingStep1Fragment extends Fragment {

    private FragmentOnboardingStep1Binding binding;
    private OnboardingViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOnboardingStep1Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(OnboardingViewModel.class);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, 
                getResources().getStringArray(R.array.gender_array));
        binding.spinnerGender.setAdapter(adapter);

        // Ensure it shows all options when clicked
        binding.spinnerGender.setOnClickListener(v -> binding.spinnerGender.showDropDown());

        // Pre-fill if exists
        if (viewModel.name.getValue() != null) {
            binding.etName.setText(viewModel.name.getValue());
        }
        if (viewModel.age.getValue() != null) {
            binding.etAge.setText(String.valueOf(viewModel.age.getValue()));
        }

        binding.btnContinue.setOnClickListener(v -> {
            String name = binding.etName.getText().toString();
            String ageStr = binding.etAge.getText().toString();
            
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(requireContext(), "Please enter your name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(ageStr)) {
                Toast.makeText(requireContext(), "Please enter age", Toast.LENGTH_SHORT).show();
                return;
            }
            int age = Integer.parseInt(ageStr);
            String gender = binding.spinnerGender.getText().toString();

            viewModel.name.setValue(name);
            viewModel.age.setValue(age);
            viewModel.gender.setValue(gender);

            ((OnboardingActivity) requireActivity()).goToNextStep();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
