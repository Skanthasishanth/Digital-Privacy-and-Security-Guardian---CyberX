package com.dpsg.cyberx;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LastUsageAdapter extends RecyclerView.Adapter<LastUsageAdapter.LastUsageViewHolder> {

    private final Context context;
    private final List<LastUsage> usageList;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    public LastUsageAdapter(Context context, List<LastUsage> usageList) {
        this.context = context;
        this.usageList = usageList;
    }

    @NonNull
    @Override
    public LastUsageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Reuse the same detail layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_timeline_detail, parent, false);
        return new LastUsageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LastUsageViewHolder holder, int position) {
        LastUsage usage = usageList.get(position);

        // Calculate and format duration
        long durationMillis = usage.lastEndTime - usage.lastStartTime;
        String durationString = formatDuration(durationMillis);

        // Format start/end times
        String startTime = timeFormat.format(new Date(usage.lastStartTime));
        String endTime = timeFormat.format(new Date(usage.lastEndTime));

        // Bind data
        holder.appName.setText(usage.appName);
        holder.appIcon.setImageDrawable(usage.appIcon);

        // "Total Duration" will now represent the last single session duration
        holder.durationText.setText(String.format("Last Duration: %s", durationString));

        // "10:07 AM to 10:10 AM" is the exact format you requested!
        holder.timesText.setText(String.format("Lastly Used: %s to %s", startTime, endTime));
    }

    @Override
    public int getItemCount() {
        return usageList.size();
    }

    /** Helper to format time. Duplicated from previous step for file completeness. */
    private String formatDuration(long durationMillis) {
        long seconds = (durationMillis / 1000) % 60;
        long minutes = (durationMillis / (1000 * 60)) % 60;

        if (minutes > 0) {
            return String.format(Locale.getDefault(), "%dm %02ds", minutes, seconds);
        } else {
            return String.format(Locale.getDefault(), "%ds", seconds);
        }
    }

    public static class LastUsageViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView durationText;
        TextView timesText;

        public LastUsageViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.timeline_app_icon);
            appName = itemView.findViewById(R.id.timeline_app_name);
            durationText = itemView.findViewById(R.id.timeline_duration);
            timesText = itemView.findViewById(R.id.timeline_times);
        }
    }
}