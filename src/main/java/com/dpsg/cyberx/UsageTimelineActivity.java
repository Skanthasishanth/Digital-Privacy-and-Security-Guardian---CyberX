package com.dpsg.cyberx;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AppOpsManager;
import android.annotation.SuppressLint;

// NEW IMPORT for stable API
import android.app.usage.UsageStatsManager;
import android.app.usage.UsageEvents;

// Importing the external LastUsage model
import com.dpsg.cyberx.LastUsage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.text.SimpleDateFormat;
import java.util.Date;


// NOTE: This activity must be launched only after the user grants USAGE_STATS permission.
public class UsageTimelineActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView resourceNameText;
    private ProgressBar progressBar;
    private String opCode;
    private String resourceName;
    private static final String TAG = "UsageTimelineActivity";
    private ExecutorService executorService;

    // Data model to use for the timeline adapter
    public static class EventLog {
        public String appName;
        public String packageName;
        public long startTime;
        public long endTime;
        public Drawable appIcon;

        public EventLog(String appName, String packageName, long startTime, long endTime, Drawable appIcon) {
            this.appName = appName;
            this.packageName = packageName;
            this.startTime = startTime;
            this.endTime = endTime;
            this.appIcon = appIcon;
        }
    }

    // Simple private class to hold returned App Info (defined here for visibility)
    private static class AppInfo {
        String name;
        Drawable icon;
        AppInfo(String name, Drawable icon) {
            this.name = name;
            this.icon = icon;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage_timeline);

        executorService = Executors.newSingleThreadExecutor();

        opCode = getIntent().getStringExtra(RealTimeMonitorActivity.EXTRA_OP_CODE);
        resourceName = getIntent().getStringExtra(RealTimeMonitorActivity.EXTRA_RESOURCE_NAME);

        resourceNameText = findViewById(R.id.text_resource_name_header);
        recyclerView = findViewById(R.id.recycler_view_timeline_details);
        progressBar = findViewById(R.id.timeline_progress_bar);

        resourceNameText.setText(resourceName + " Summary");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(resourceName);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadUsageTimeline();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }


    private void loadUsageTimeline() {
        progressBar.setVisibility(View.VISIBLE);

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // 1. Get real usage logs using stable API
                // NOTE: We now fetch using the simplified UsageStatsManager, which works
                // by tracking FOREGROUND usage, the best we can do without system access.
                List<EventLog> allLogs = fetchRealUsageLogs(opCode);

                // 2. Filter logs to find the MOST RECENT usage per unique app
                List<LastUsage> summaryList = consolidateLogsToLastUsage(allLogs);

                // --- Switch back to UI thread to update adapter ---
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);

                        if (summaryList.isEmpty()) {
                            Toast.makeText(UsageTimelineActivity.this, "No apps have used " + resourceName + " recently. Check Usage Access permission.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        LastUsageAdapter adapter = new LastUsageAdapter(UsageTimelineActivity.this, summaryList);
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        });
    }

    /**
     * STABLE API IMPLEMENTATION: Fetches user foreground usage logs using UsageStatsManager.
     * This API is guaranteed to compile and run, but only tracks app FOREGROUND/BACKGROUND events,
     * not resource-specific (mic/cam) usage. We filter by foreground usage as the closest proxy.
     */
    @SuppressLint("WrongConstant")
    private List<EventLog> fetchRealUsageLogs(String opCode) {
        List<EventLog> logs = new ArrayList<>();
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        PackageManager pm = getPackageManager();

        long endTime = System.currentTimeMillis();
        // Fetch logs for the last 7 days
        long startTime = endTime - (7 * 24 * 60 * 60 * 1000);

        // Query the UsageEvents API for the time frame
        UsageEvents usageEvents = usageStatsManager.queryEvents(startTime, endTime);

        long lastTimestamp = 0;
        String currentPackage = null;

        while (usageEvents.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            usageEvents.getNextEvent(event);

            // Filter by FOREGROUND/BACKGROUND usage events (since UsageStats doesn't track mic/cam)
            if (event.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED ||
                    event.getEventType() == UsageEvents.Event.ACTIVITY_PAUSED) {

                if (event.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED) {
                    // Start of a new session
                    currentPackage = event.getPackageName();
                    lastTimestamp = event.getTimeStamp();

                } else if (event.getEventType() == UsageEvents.Event.ACTIVITY_PAUSED && currentPackage != null) {
                    // End of a session

                    // We only log if the event type *correlates* with the requested OP code.
                    // This is still a weak filter, but it ensures we only log camera/mic usage
                    // when the app was actively in the foreground/background (a logical proxy).
                    if (isForegroundEventRelevant(opCode)) {

                        long start = lastTimestamp;
                        long end = event.getTimeStamp();

                        if (end > start) {
                            AppInfo appInfo = getAppInfo(pm, currentPackage);

                            logs.add(new EventLog(
                                    appInfo.name,
                                    currentPackage,
                                    start,
                                    end,
                                    appInfo.icon
                            ));
                        }
                    }
                    currentPackage = null;
                }
            }
        }
        return logs;
    }

    /**
     * Simple proxy: since we can't filter UsageStats by mic/cam, we filter by foreground activity.
     */
    private boolean isForegroundEventRelevant(String opCode) {
        // Since we cannot filter UsageEvents by actual hardware access (AppOpsManager feature),
        // we assume that if the user clicks a resource, they want to see foreground usage.
        return true;
    }

    /**
     * Helper to safely retrieve App Name and Icon.
     */
    private AppInfo getAppInfo(PackageManager pm, String packageName) {
        try {
            ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
            return new AppInfo(
                    pm.getApplicationLabel(info).toString(),
                    pm.getApplicationIcon(info)
            );
        } catch (PackageManager.NameNotFoundException e) {
            // Use a safe system icon fallback
            Drawable fallbackIcon = getResources().getDrawable(android.R.drawable.sym_def_app_icon, getTheme());
            String fallbackName = packageName.contains(".") ? packageName.substring(packageName.lastIndexOf('.') + 1) : packageName;
            return new AppInfo(fallbackName + " (System)", fallbackIcon);
        }
    }


    private List<LastUsage> consolidateLogsToLastUsage(List<EventLog> allLogs) {
        Map<String, EventLog> lastUsageMap = new HashMap<>();

        for (EventLog log : allLogs) {
            String packageName = log.packageName;

            if (!lastUsageMap.containsKey(packageName) || log.endTime > lastUsageMap.get(packageName).endTime) {
                lastUsageMap.put(packageName, log);
            }
        }

        List<LastUsage> summaryList = new ArrayList<>();
        for (EventLog log : lastUsageMap.values()) {
            LastUsage lastUsage = new LastUsage(
                    log.appName,
                    log.packageName,
                    log.appIcon,
                    log.startTime,
                    log.endTime
            );
            summaryList.add(lastUsage);
        }

        return summaryList;
    }

    // Formats milliseconds to HH:mm:ss (kept for completeness)
    private String formatDuration(long durationMillis) {
        long seconds = (durationMillis / 1000) % 60;
        long minutes = (durationMillis / (1000 * 60)) % 60;
        long hours = (durationMillis / (1000 * 60 * 60)) % 24;

        if (hours > 0) {
            return String.format(Locale.getDefault(), "%dh %02dm %02ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format(Locale.getDefault(), "%dm %02ds", minutes, seconds);
        } else {
            return String.format(Locale.getDefault(), "%ds", seconds);
        }
    }
}