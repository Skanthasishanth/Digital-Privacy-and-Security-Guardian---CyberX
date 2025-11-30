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
import java.util.concurrent.TimeUnit;
import java.util.Locale;

public class AlertsAdapter extends RecyclerView.Adapter<AlertsAdapter.AlertViewHolder> {

    private final Context context;
    private final List<AlertsActivity.Alert> alertList;

    // Define colors used in the layout (must match colors.xml)
    private static final int COLOR_HIGH = R.color.design_default_color_error; // Red
    private static final int COLOR_MEDIUM = R.color.purple_500; // Purple
    private static final int COLOR_LOW = R.color.design_default_color_secondary; // Green
    private static final int COLOR_TEXT_DEFAULT = R.color.black;

    public AlertsAdapter(Context context, List<AlertsActivity.Alert> alertList) {
        this.context = context;
        this.alertList = alertList;
    }

    @NonNull
    @Override
    public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_alert_detail, parent, false);
        return new AlertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertViewHolder holder, int position) {
        AlertsActivity.Alert alert = alertList.get(position);

        // Determine colors and icon tint based on severity
        int severityColorResId;
        int iconResId;

        switch (alert.severity) {
            case "HIGH":
                severityColorResId = COLOR_HIGH;
                iconResId = R.drawable.ic_warning_red; // Reusing red warning icon
                break;
            case "MEDIUM":
                severityColorResId = COLOR_MEDIUM;
                iconResId = R.drawable.ic_camera; // Using camera icon as a generic medium alert
                break;
            case "LOW":
                severityColorResId = COLOR_LOW;
                iconResId = R.drawable.ic_storage; // Using storage icon as a generic low alert
                break;
            default:
                severityColorResId = COLOR_TEXT_DEFAULT;
                iconResId = R.drawable.ic_warning_red;
        }

        // Apply colors and text
        holder.title.setText(alert.title);
        holder.description.setText(alert.description);

        // Change description color for emphasis on high severity
        holder.description.setTextColor(context.getResources().getColor(severityColorResId, context.getTheme()));

        // Set icon
        holder.icon.setImageResource(iconResId);
        holder.icon.setColorFilter(context.getResources().getColor(severityColorResId, context.getTheme()));

        // Set timestamp
        holder.timestamp.setText(formatTimeAgo(alert.timestamp));

        // Optional: Highlight card background for high alerts
        if (alert.severity.equals("HIGH")) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.warning_background, context.getTheme()));
        } else {
            holder.itemView.setBackgroundColor(context.getResources().getColor(android.R.color.white, context.getTheme()));
        }
    }

    @Override
    public int getItemCount() {
        return alertList.size();
    }

    /**
     * Helper function to format timestamp into "X minutes ago"
     */
    private String formatTimeAgo(long timeMillis) {
        long diff = System.currentTimeMillis() - timeMillis;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        long hours = TimeUnit.MILLISECONDS.toHours(diff);
        long days = TimeUnit.MILLISECONDS.toDays(diff);

        if (days > 0) {
            return String.format(Locale.getDefault(), "%d days ago", days);
        } else if (hours > 0) {
            return String.format(Locale.getDefault(), "%d hours ago", hours);
        } else if (minutes > 0) {
            return String.format(Locale.getDefault(), "%d minutes ago", minutes);
        } else {
            return "Just now";
        }
    }

    public static class AlertViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        TextView description;
        TextView timestamp;

        public AlertViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.alert_icon);
            title = itemView.findViewById(R.id.alert_title);
            description = itemView.findViewById(R.id.alert_description);
            timestamp = itemView.findViewById(R.id.alert_timestamp);
        }
    }
}