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
import java.util.Locale; // Used for time formatting
import java.util.Date; // Used for time formatting
import java.text.SimpleDateFormat; // Used for time formatting

// Imported the correct data model from its parent activity
import com.dpsg.cyberx.UsageTimelineActivity.EventLog;

// Define constants using the imported resource IDs
import static com.dpsg.cyberx.R.drawable.ic_warning_red;

public class UsageTimelineAdapter extends RecyclerView.Adapter<UsageTimelineAdapter.UsageViewHolder> {

    private final Context context;
    private final List<EventLog> eventList;

    // Initialize SimpleDateFormat once for efficiency
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    public UsageTimelineAdapter(Context context, List<EventLog> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public UsageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_timeline_detail, parent, false);
        return new UsageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsageViewHolder holder, int position) {
        EventLog log = eventList.get(position);

        // 1. Calculate and format duration (long to String)
        long durationMillis = log.endTime - log.startTime;
        String durationString = formatDuration(durationMillis);

        // 2. Format start/end times (long to hh:mm AM/PM)
        String startTime = timeFormat.format(new Date(log.startTime));
        String endTime = timeFormat.format(new Date(log.endTime));

        // 3. Bind data to the ViewHolder fields
        holder.appName.setText(log.appName);
        holder.appIcon.setImageDrawable(log.appIcon);

        // Correct binding for duration
        holder.durationText.setText(String.format("Total Duration: %s", durationString));

        // Correct binding for time range
        holder.timesText.setText(String.format("%s to %s", startTime, endTime));

        // NOTE: holder.resourceText is not bound as it's not part of the EventLog model
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    /**
     * Helper method to convert milliseconds into a human-readable duration string.
     */
    private String formatDuration(long durationMillis) {
        long seconds = (durationMillis / 1000) % 60;
        long minutes = (durationMillis / (1000 * 60)) % 60;
        long hours = (durationMillis / (1000 * 60 * 60)) % 24;

        if (hours > 0) {
            return String.format(Locale.getDefault(), "%dh %02dm", hours, minutes);
        } else if (minutes > 0) {
            return String.format(Locale.getDefault(), "%dm %02ds", minutes, seconds);
        } else {
            // Use seconds if duration is less than a minute
            return String.format(Locale.getDefault(), "%ds", seconds);
        }
    }

    public static class UsageViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView durationText;
        TextView timesText;
        // Note: resourceText is ignored here as it's not used by the model fields

        public UsageViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.timeline_app_icon);
            appName = itemView.findViewById(R.id.timeline_app_name);
            durationText = itemView.findViewById(R.id.timeline_duration);
            timesText = itemView.findViewById(R.id.timeline_times);
            // We ignore R.id.text_resource_used here to avoid a crash if it was in the layout
        }
    }
}