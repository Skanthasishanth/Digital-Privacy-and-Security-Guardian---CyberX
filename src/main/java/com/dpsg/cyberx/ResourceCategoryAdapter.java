package com.dpsg.cyberx;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// 1. ResourceCategory Model
class ResourceCategory {
    public String name;
    public String opCode; // The unique identifier for AppOpsManager
    public int iconResId;
    public int usageCount;

    public ResourceCategory(String name, String opCode, int iconResId, int usageCount) {
        this.name = name;
        this.opCode = opCode;
        this.iconResId = iconResId;
        this.usageCount = usageCount;
    }
}

public class ResourceCategoryAdapter extends RecyclerView.Adapter<ResourceCategoryAdapter.CategoryViewHolder> {

    private final Context context;
    private final List<ResourceCategory> categoryList;
    private final OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(ResourceCategory category);
    }

    public ResourceCategoryAdapter(Context context, List<ResourceCategory> categoryList, OnCategoryClickListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_resource_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        ResourceCategory category = categoryList.get(position);

        holder.title.setText(category.name);
        holder.icon.setImageResource(category.iconResId);
        holder.count.setText(String.format("%d events", category.usageCount));

        holder.itemView.setOnClickListener(v -> listener.onCategoryClick(category));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        TextView count;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.category_icon);
            title = itemView.findViewById(R.id.category_title);
            count = itemView.findViewById(R.id.category_count);
        }
    }
}