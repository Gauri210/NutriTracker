package com.nutritracker.ui.dashboard;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nutritracker.R;
import com.nutritracker.data.local.entity.MealDetailDto;

import java.util.List;
import java.util.Locale;

public class MealAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    
    private List<Object> items;

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<Object> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof String) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal_detail, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_HEADER) {
            String headerTitle = (String) items.get(position);
            ((HeaderViewHolder) holder).tvHeader.setText(headerTitle);
        } else {
            MealDetailDto detail = (MealDetailDto) items.get(position);
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            
            itemHolder.tvFoodName.setText(detail.foodName);
            itemHolder.tvQuantity.setText(String.format(Locale.getDefault(), "%s %s", detail.quantity, detail.unitName));
            
            int cals = (int)((detail.caloriesPer100g / 100.0) * (detail.unitGrams * detail.quantity));
            itemHolder.tvCalories.setText(String.format(Locale.getDefault(), "%d kcal", cals));
        }
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;
        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tvHeader);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodName, tvQuantity, tvCalories;
        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvCalories = itemView.findViewById(R.id.tvCalories);
        }
    }
}
