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
import java.util.Date;
import java.text.SimpleDateFormat;

// Import the public inner class correctly
import com.dpsg.cyberx.UsageTimelineActivity.EventLog;

// ** NEW FIX: Import the static R class for drawable resources **
import static com.dpsg.cyberx.R.drawable.ic_camera;
import static com.dpsg.cyberx.R.drawable.ic_mic;
import static com.dpsg.cyberx.R.drawable.ic_location;
import static com.dpsg.cyberx.R.drawable.ic_storage;
import static com.dpsg.cyberx.R.drawable.ic_warning_red;
// ---------------------------------------------------------------------

public class TimelineDetailAdapter extends RecyclerView.Adapter<TimelineDetailAdapter.TimelineViewHolder> {

    private final Context context;
    private final List<EventLog> eventList;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    // Define constants using the imported resource IDs
    private static final int IC_MIC = ic_mic;
    private static final int IC_CAMERA = ic_camera;
    private static final int IC_LOCATION = ic_location;
    private static final int IC_STORAGE = ic_storage;
    private static final int IC_WARNING = ic_warning_red;

    public TimelineDetailAdapter(Context context, List<EventLog> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public TimelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_timeline_detail, parent, false);
        return new TimelineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineViewHolder holder, int position) {
        EventLog log = eventList.get(position);

        // Calculate and format duration
        long durationMillis = log.endTime - log.startTime;
        String durationString = formatDuration(durationMillis);

        // Format start/end times
        String startTime = timeFormat.format(new Date(log.startTime));
        String endTime = timeFormat.format(new Date(log.endTime));

        // Bind data
        holder.appName.setText(log.appName);
        holder.appIcon.setImageDrawable(log.appIcon);
        holder.durationText.setText(String.format("Total Duration: %s", durationString));
        holder.timesText.setText(String.format("%s to %s", startTime, endTime));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    // Formats milliseconds to a human-readable duration string
    private String formatDuration(long durationMillis) {
        long seconds = (durationMillis / 1000) % 60;
        long minutes = (durationMillis / (1000 * 60)) % 60;
        long hours = (durationMillis / (1000 * 60 * 60)) % 24;

        if (hours > 0) {
            return String.format(Locale.getDefault(), "%dh %02dm", hours, minutes);
        } else if (minutes > 0) {
            return String.format(Locale.getDefault(), "%dm %02ds", minutes, seconds);
        } else {
            return String.format(Locale.getDefault(), "%ds", seconds);
        }
    }

    public static class TimelineViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView durationText;
        TextView timesText;

        public TimelineViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.timeline_app_icon);
            appName = itemView.findViewById(R.id.timeline_app_name);
            durationText = itemView.findViewById(R.id.timeline_duration);
            timesText = itemView.findViewById(R.id.timeline_times);
        }
    }
}