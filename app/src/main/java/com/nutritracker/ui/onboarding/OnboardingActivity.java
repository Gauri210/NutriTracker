package com.nutritracker.ui.onboarding;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.nutritracker.R;
import com.nutritracker.databinding.ActivityOnboardingBinding;
import com.nutritracker.databinding.DialogGoalsSummaryBinding;
import com.nutritracker.ui.dashboard.MainActivity;
import com.nutritracker.viewmodel.OnboardingViewModel;

import java.util.Locale;

public class OnboardingActivity extends AppCompatActivity {

    private ActivityOnboardingBinding binding;
    private int currentStep = 1;
    private OnboardingViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(OnboardingViewModel.class);
        
        // Skip onboarding if user exists
        viewModel.getUser().observe(this, user -> {
            if (user != null && currentStep == 1) { // Very basic check to hop fast if user data present
                navigateToDashboard();
            }
        });

        if (savedInstanceState == null) {
            loadFragment(new OnboardingStep1Fragment());
            updateProgressUI();
        }
    }

    public void goToNextStep() {
        if (currentStep == 1) {
            currentStep = 2;
            loadFragment(new OnboardingStep2Fragment());
        } else if (currentStep == 2) {
            currentStep = 3;
            loadFragment(new OnboardingStep3Fragment());
        }
        updateProgressUI();
    }

    public void goToPreviousStep() {
        if (currentStep == 3) {
            currentStep = 2;
            loadFragment(new OnboardingStep2Fragment());
        } else if (currentStep == 2) {
            currentStep = 1;
            loadFragment(new OnboardingStep1Fragment());
        }
        updateProgressUI();
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }

    public void showSummaryDialogAndSave() {
        com.nutritracker.data.local.entity.UserEntity draft = viewModel.generateDraftUser();

        DialogGoalsSummaryBinding dialogBinding = DialogGoalsSummaryBinding.inflate(getLayoutInflater());
        
        dialogBinding.tvCalorieTarget.setText(String.format(Locale.getDefault(), "%,d kcal", draft.dailyCalorieTarget));
        dialogBinding.tvTdee.setText(String.format(Locale.getDefault(), "%,d kcal", (int)draft.tdee));
        dialogBinding.tvGoalDate.setText(draft.estimatedGoalDate);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogBinding.getRoot())
                .setCancelable(false)
                .create();

        // Make background transparent to show rounded corners of the card
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialogBinding.btnLetsGo.setOnClickListener(v -> {
            viewModel.saveUserData(draft);
            dialog.dismiss();
            navigateToDashboard();
        });

        dialog.show();
    }

    private void navigateToDashboard() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void updateProgressUI() {
        binding.tvStepText.setText("Step " + currentStep + " of 3");

        binding.step1Indicator.setBackgroundResource(currentStep >= 1 ? R.drawable.pill_shape_active : R.drawable.pill_shape_inactive);
        binding.step2Indicator.setBackgroundResource(currentStep >= 2 ? R.drawable.pill_shape_active : R.drawable.pill_shape_inactive);
        binding.step3Indicator.setBackgroundResource(currentStep >= 3 ? R.drawable.pill_shape_active : R.drawable.pill_shape_inactive);
    }
}
