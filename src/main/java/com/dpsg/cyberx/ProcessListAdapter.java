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
import java.util.Locale;

public class ProcessListAdapter extends RecyclerView.Adapter<ProcessListAdapter.ProcessViewHolder> {

    private final Context context;
    private final List<SystemProcess> processList;

    public ProcessListAdapter(Context context, List<SystemProcess> processList) {
        this.context = context;
        this.processList = processList;
    }

    @NonNull
    @Override
    public ProcessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // We'll define a new layout for the process list in the next step
        View view = LayoutInflater.from(context).inflate(R.layout.item_process_detail, parent, false);
        return new ProcessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProcessViewHolder holder, int position) {
        SystemProcess process = processList.get(position);

        // Bind data
        holder.appName.setText(process.name);
        holder.appIcon.setImageDrawable(process.icon);
        holder.cpuUsage.setText(String.format(Locale.getDefault(), "%d %% CPU", process.cpuUsagePercent));
        holder.memoryUsage.setText(formatMemory(process.memoryUsageKb));
        holder.packageName.setText(process.packageName);

        // NEW: Bind Network Data (Using the timesText field from the process detail item)
        // Since item_process_detail doesn't have dedicated network fields, we'll append to the package name view or ignore for now.
        // For visual representation, let's update the description to show data transfer.
        holder.networkUsage.setText(String.format(Locale.getDefault(), "Sent: %s / Recv: %s",
                formatBytes(process.bytesSent),
                formatBytes(process.bytesReceived)));


        // Highlight high resource users (mock rule)
        if (process.cpuUsagePercent > 10 || process.memoryUsageKb > 500000 || process.bytesSent > 10000000) { // Added network check
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.warning_background, context.getTheme()));
        } else {
            holder.itemView.setBackgroundColor(context.getResources().getColor(android.R.color.white, context.getTheme()));
        }
    }

    @Override
    public int getItemCount() {
        return processList.size();
    }

    // Helper to format memory from KB to MB or GB
    private String formatMemory(long memoryKb) {
        if (memoryKb > 1024 * 1024) {
            return String.format(Locale.getDefault(), "%.2f GB", memoryKb / (1024f * 1024f));
        } else if (memoryKb > 1024) {
            return String.format(Locale.getDefault(), "%.2f MB", memoryKb / 1024f);
        } else {
            return String.format(Locale.getDefault(), "%d KB", memoryKb);
        }
    }

    /** Helper: Format bytes to KB, MB, GB */
    private String formatBytes(long bytes) {
        if (bytes <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
        if (digitGroups >= units.length) {
            digitGroups = units.length - 1;
        }
        return String.format(Locale.getDefault(), "%.2f %s", bytes / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    public static class ProcessViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView packageName;
        TextView cpuUsage;
        TextView memoryUsage;
        TextView networkUsage; // NEW FIELD (mapping to package name's TextView for now)

        public ProcessViewHolder(@NonNull View itemView) {
            super(itemView);
            appName = itemView.findViewById(R.id.process_app_name);
            appIcon = itemView.findViewById(R.id.process_app_icon);
            packageName = itemView.findViewById(R.id.process_package_name);
            cpuUsage = itemView.findViewById(R.id.process_cpu_usage);
            memoryUsage = itemView.findViewById(R.id.process_memory_usage);
            // Reusing packageName TextView to display network usage metrics due to XML constraints.
            networkUsage = itemView.findViewById(R.id.process_package_name);
        }
    }
}