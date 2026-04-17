package com.nutritracker.ui.logging;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nutritracker.data.local.entity.FoodItem;
import com.nutritracker.databinding.ItemFoodSearchBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FoodSearchResultAdapter extends RecyclerView.Adapter<FoodSearchResultAdapter.FoodViewHolder> {

    private List<FoodItem> items = new ArrayList<>();
    private final OnFoodClickListener listener;

    public interface OnFoodClickListener {
        void onFoodClick(FoodItem foodItem);
    }

    public FoodSearchResultAdapter(OnFoodClickListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void submitList(List<FoodItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFoodSearchBinding binding = ItemFoodSearchBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FoodViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem item = items.get(position);
        holder.binding.tvFoodName.setText(item.name);
        holder.binding.tvFoodMacros.setText(String.format(Locale.getDefault(), "%.0f kcal per 100g", item.caloriesPer100g));
        holder.itemView.setOnClickListener(v -> listener.onFoodClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ItemFoodSearchBinding binding;

        FoodViewHolder(ItemFoodSearchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
