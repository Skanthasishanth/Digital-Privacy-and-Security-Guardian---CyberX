package com.dpsg.cyberx;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PermissionDetailAdapter extends RecyclerView.Adapter<PermissionDetailAdapter.PermissionViewHolder> {

    private final Context context;
    private final List<String> permissionList;
    private final OnPermissionActionListener listener;

    // Interface to handle click events in the Activity
    public interface OnPermissionActionListener {
        void onManagePermissionClick(String permissionName);
    }

    public PermissionDetailAdapter(Context context, List<String> permissionList, OnPermissionActionListener listener) {
        this.context = context;
        this.permissionList = permissionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PermissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_permission_detail, parent, false);
        return new PermissionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PermissionViewHolder holder, int position) {
        String permissionName = permissionList.get(position);

        // Clean up the permission name for display
        String shortName = permissionName.replace("android.permission.", "");

        holder.permissionName.setText(shortName);

        // Set the click listener on the Manage Button
        holder.manageButton.setOnClickListener(v -> {
            listener.onManagePermissionClick(permissionName);
        });

        // Optionally, make dangerous permissions redder (if we had complex logic)
        // Since we only show GRANTED dangerous permissions here, we keep the red tint from the XML.
    }

    @Override
    public int getItemCount() {
        return permissionList.size();
    }

    public static class PermissionViewHolder extends RecyclerView.ViewHolder {
        TextView permissionName;
        Button manageButton;

        public PermissionViewHolder(@NonNull View itemView) {
            super(itemView);
            permissionName = itemView.findViewById(R.id.text_permission_name);
            manageButton = itemView.findViewById(R.id.btn_manage_permission);
            // Note: icon_status is in the layout but not strictly necessary to control in Java for this step
        }
    }
}