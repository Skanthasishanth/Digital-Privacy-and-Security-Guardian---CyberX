package com.dpsg.cyberx;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.AppViewHolder> {

    private final Context context;
    private final List<InstalledApp> appList;
    private final OnAppClickListener listener;

    // Interface to handle click events in the Activity
    public interface OnAppClickListener {
        void onAppClick(InstalledApp app);
    }

    public AppListAdapter(Context context, List<InstalledApp> appList, OnAppClickListener listener) {
        this.context = context;
        this.appList = appList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_app_permission, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        InstalledApp app = appList.get(position);

        // Bind data to the views in the item_app_permission layout
        holder.appName.setText(app.getName());
        holder.appPackageName.setText(app.getPackageName());
        holder.appIcon.setImageDrawable(app.getIcon());
        holder.permissionCount.setText(String.valueOf(app.getGrantedPermissionsCount()));

        // Set the click listener on the entire CardView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pass the clicked app back to the Activity
                listener.onAppClick(app);
            }
        });
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    // ViewHolder class to hold the views for each item
    public static class AppViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView appPackageName;
        TextView permissionCount;

        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon);
            appName = itemView.findViewById(R.id.app_name);
            appPackageName = itemView.findViewById(R.id.app_package_name);
            permissionCount = itemView.findViewById(R.id.permission_count);
        }
    }
}