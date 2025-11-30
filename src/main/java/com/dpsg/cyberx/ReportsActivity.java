package com.dpsg.cyberx;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Debug;
import android.net.TrafficStats; // CORRECTED IMPORT
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.app.usage.NetworkStatsManager;
import android.app.usage.NetworkStats;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.content.Intent;
import android.app.AppOpsManager;
import android.app.usage.UsageStatsManager; // NEW IMPORT
import android.app.usage.UsageStats;       // NEW IMPORT


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.Map;

public class ReportsActivity extends AppCompatActivity {

    private static final String TAG = "ReportsActivity";
    private TextView scoreText;
    private TextView ramUsageText;
    private TextView cpuUsageText;
    private TextView dataSentText;
    private TextView dataReceivedText;
    private TextView dataHogsHeader;
    private TextView activeAppsHeader; // NEW UI FIELD
    private RecyclerView processRecyclerView;
    private RecyclerView dataHogsRecyclerView;
    private RecyclerView activeAppsRecyclerView; // NEW UI FIELD
    private ExecutorService executorService;
    private ProcessListAdapter adapter;
    private ProcessListAdapter dataHogAdapter;
    private ProcessListAdapter activeAppsAdapter; // NEW ADAPTER FIELD
    private NetworkStatsManager networkStatsManager;
    private UsageStatsManager usageStatsManager; // NEW FIELD

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        executorService = Executors.newSingleThreadExecutor();
        networkStatsManager = (NetworkStatsManager) getSystemService(Context.NETWORK_STATS_SERVICE);
        usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE); // INITIALIZE

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Privacy Reports");
        }

        // 1. Initialize UI components
        scoreText = findViewById(R.id.text_security_score);
        ramUsageText = findViewById(R.id.text_ram_usage);
        cpuUsageText = findViewById(R.id.text_cpu_usage);
        dataSentText = findViewById(R.id.text_data_sent);
        dataReceivedText = findViewById(R.id.text_data_received);
        processRecyclerView = findViewById(R.id.recycler_view_processes);
        dataHogsRecyclerView = findViewById(R.id.recycler_view_data_hogs);
        dataHogsHeader = findViewById(R.id.text_data_hogs_header); // INITIALIZE NEW HEADER
        activeAppsHeader = findViewById(R.id.text_active_apps_header); // INITIALIZE NEW HEADER
        activeAppsRecyclerView = findViewById(R.id.recycler_view_active_apps); // INITIALIZE NEW RECYCLERVIEW

        // Setup RecyclerViews
        processRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataHogsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        activeAppsRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // SETUP NEW RECYCLERVIEW

        // 2. Start data loading
        loadSystemMetrics();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }

    private void loadSystemMetrics() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // Fetch all running processes using UsageStatsManager (including recently active)
                List<SystemProcess> allRecentProcesses = fetchRunningProcesses();

                // Categorize processes into active and background
                CategorizedProcesses categorized = categorizeProcesses(allRecentProcesses);
                List<SystemProcess> activeProcesses = categorized.getActiveApps();
                List<SystemProcess> backgroundProcesses = categorized.getBackgroundApps();

                // --- METRIC CALCULATION (UNCHANGED, now based on allRecentProcesses) ---

                long totalUsedRamKb = 0;
                long totalDataSent = 0;
                long totalDataReceived = 0;
                int highCpuCount = 0;

                ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
                activityManager.getMemoryInfo(memoryInfo);
                long totalSystemRamKb = memoryInfo.totalMem / 1024;

                // Sort the backgroundProcesses list by Memory Usage (for the main list view)
                Collections.sort(backgroundProcesses, new Comparator<SystemProcess>() {
                    @Override
                    public int compare(SystemProcess p1, SystemProcess p2) {
                        return Long.compare(p2.memoryUsageKb, p1.memoryUsageKb);
                    }
                });

                // Sort active processes by CPU usage or most recent usage
                Collections.sort(activeProcesses, new Comparator<SystemProcess>() {
                    @Override
                    public int compare(SystemProcess p1, SystemProcess p2) {
                        // For active apps, maybe sort by CPU or total network usage
                        long p1Total = p1.bytesReceived + p1.bytesSent;
                        long p2Total = p2.bytesReceived + p2.bytesSent;
                        return Long.compare(p2Total, p1Total); // Sort by total data usage descending
                    }
                });


                SystemProcess topDataHogOverall = null; // For scoring rule 4, based on cumulative TrafficStats
                if (!allRecentProcesses.isEmpty()) {
                    // Find the top hog based on cumulative TrafficStats for scoring purposes from all processes
                    topDataHogOverall = Collections.max(allRecentProcesses, new Comparator<SystemProcess>() {
                        @Override
                        public int compare(SystemProcess p1, SystemProcess p2) {
                            long p1Total = p1.bytesReceived + p1.bytesSent;
                            long p2Total = p2.bytesReceived + p2.bytesSent;
                            return Long.compare(p1Total, p2Total);
                        }
                    });
                }


                int overallCpu = 25; // Mock overall CPU usage (Feasibility limit)
                int score = 100;

                for (SystemProcess p : allRecentProcesses) {
                    totalUsedRamKb += p.memoryUsageKb;
                    if (p.cpuUsagePercent > 10) highCpuCount++;
                    totalDataSent += p.bytesSent;
                    totalDataReceived += p.bytesReceived;
                }

                // Apply scoring rules
                score -= highCpuCount * 5;
                if (allRecentProcesses.size() > 50) score -= 10;
                long usedRamPercent = (totalUsedRamKb * 100) / totalSystemRamKb;
                if (usedRamPercent > 85) score -= 20;

                // Rule 4: Top Network Data Hog deduction (still based on cumulative TrafficStats for consistency with original)
                if (topDataHogOverall != null) {
                    long topHogTrafficBytes = topDataHogOverall.bytesSent + topDataHogOverall.bytesReceived;
                    if (topHogTrafficBytes > 50 * 1024 * 1024) { // 50 MB
                        score -= 15;
                        Log.w(TAG, "Network Security Alert: Top Hog is " + topDataHogOverall.name + " with " + formatBytes(topHogTrafficBytes) + "."); // FIXED: .name
                    }
                }

                if (score < 50) score = 50;
                if (score > 100) score = 100;


                // --- NEW: Fetch Top 5 Data Hogs for Last 24 Hours ---
                List<SystemProcess> top5Hogs24Hours = new ArrayList<>();
                boolean hasPermission = checkUsageStatsPermission();
                if (hasPermission) {
                    long endTime = System.currentTimeMillis();
                    long startTime = endTime - (24 * 60 * 60 * 1000); // Last 24 hours
                    top5Hogs24Hours = fetchNetworkUsageForApps(startTime, endTime);
                } else {
                    runOnUiThread(() -> {
                        dataHogsHeader.setText("Top 5 Data Hogs (Permission Required)");
                        Toast.makeText(ReportsActivity.this, "Usage Access permission is required for 24-hour network stats.", Toast.LENGTH_LONG).show();
                        // Optionally, show a button to open settings
                    });
                }


                // --- Switch back to UI thread to update the view ---

                final List<SystemProcess> finalActiveProcesses = activeProcesses;
                final List<SystemProcess> finalBackgroundProcesses = backgroundProcesses;
                final List<SystemProcess> finalTopHogs24Hours = top5Hogs24Hours;
                final long finalTotalRamKb = totalUsedRamKb;
                final int finalScore = score;
                final int finalCpu = overallCpu;
                final long finalDataSent = totalDataSent;
                final long finalDataReceived = totalDataReceived;
                final boolean finalHasPermission = hasPermission;


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Update Summary Metrics
                        scoreText.setText(String.format(Locale.getDefault(), "%d %%", finalScore));
                        ramUsageText.setText(formatMemory(finalTotalRamKb));
                        cpuUsageText.setText(String.format(Locale.getDefault(), "%d %%", finalCpu));
                        dataSentText.setText(formatBytes(finalDataSent));
                        dataReceivedText.setText(formatBytes(finalDataReceived));

                        // Update Currently Active Apps List
                        activeAppsHeader.setText("Currently Active Apps");
                        activeAppsAdapter = new ProcessListAdapter(ReportsActivity.this, finalActiveProcesses);
                        activeAppsRecyclerView.setAdapter(activeAppsAdapter);

                        // Update Background Running Apps List (Memory Sorted)
                        // The original 'processRecyclerView' now shows background apps
                        // Its header will be updated in activity_reports.xml
                        adapter = new ProcessListAdapter(ReportsActivity.this, finalBackgroundProcesses);
                        processRecyclerView.setAdapter(adapter);

                        // Update Top 5 Data Hogs List (Network Sorted, Last 24 Hours)
                        if (finalHasPermission) {
                            dataHogsHeader.setText("Top 5 Data Hogs (Last 24 Hours)");
                        } else {
                            dataHogsHeader.setText("Top 5 Data Hogs (Permission Required)");
                        }
                        dataHogAdapter = new ProcessListAdapter(ReportsActivity.this, finalTopHogs24Hours);
                        dataHogsRecyclerView.setAdapter(dataHogAdapter);

                        if (finalScore < 70) {
                            Toast.makeText(ReportsActivity.this, "Security Score is low! Review running processes.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    /**
     * Helper method to check if USAGE_STATS permission is granted.
     * If not, it will prompt the user to open settings.
     * @return true if permission is granted, false otherwise.
     */
    private boolean checkUsageStatsPermission() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        if (mode == AppOpsManager.MODE_ALLOWED) {
            return true;
        } else {
            // Prompt user to grant permission
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
            return false;
        }
    }


    /**
     * Fetches top 5 data hogs for the last 24 hours using NetworkStatsManager.
     * Requires PACKAGE_USAGE_STATS permission.
     */
    private List<SystemProcess> fetchNetworkUsageForApps(long startTime, long endTime) {
        List<SystemProcess> dataHogs = new ArrayList<>();
        PackageManager pm = getPackageManager();

        Map<String, Long> appNetworkUsage = new java.util.HashMap<>();

        try {
            // Query for WIFI data
            NetworkStats wifiStats = networkStatsManager.querySummary(
                    ConnectivityManager.TYPE_WIFI,
                    "",
                    startTime,
                    endTime
            );
            while (wifiStats.hasNextBucket()) {
                NetworkStats.Bucket bucket = new NetworkStats.Bucket();
                wifiStats.getNextBucket(bucket);
                String packageName = pm.getNameForUid(bucket.getUid());
                if (packageName != null && bucket.getRxBytes() + bucket.getTxBytes() > 0) {
                    appNetworkUsage.merge(packageName, bucket.getRxBytes() + bucket.getTxBytes(), Long::sum);
                }
            }
            wifiStats.close();

            // Query for MOBILE data
            NetworkStats mobileStats = networkStatsManager.querySummary(
                    ConnectivityManager.TYPE_MOBILE,
                    "",
                    startTime,
                    endTime
            );
            while (mobileStats.hasNextBucket()) {
                NetworkStats.Bucket bucket = new NetworkStats.Bucket();
                mobileStats.getNextBucket(bucket);
                String packageName = pm.getNameForUid(bucket.getUid());
                if (packageName != null && bucket.getRxBytes() + bucket.getTxBytes() > 0) {
                    appNetworkUsage.merge(packageName, bucket.getRxBytes() + bucket.getTxBytes(), Long::sum);
                }
            }
            mobileStats.close();

            // Convert map entries to SystemProcess objects
            for (Map.Entry<String, Long> entry : appNetworkUsage.entrySet()) {
                String packageName = entry.getKey();
                long totalBytes = entry.getValue();

                // Filter out system packages
                if (packageName != null && !packageName.contains("com.android.") && !packageName.contains("system")) {
                    String appName = packageName;
                    Drawable icon = getResources().getDrawable(R.mipmap.ic_launcher_round, getTheme());
                    try {
                        ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
                        appName = pm.getApplicationLabel(appInfo).toString();
                        icon = pm.getApplicationIcon(appInfo);
                    } catch (PackageManager.NameNotFoundException e) {
                        // Use fallback for system apps or those not found
                        Log.w(TAG, "Package name not found for: " + packageName + ", using default icon/name.");
                    }
                    // For this list, we only care about bytesSent/Received for the 24-hour period
                    dataHogs.add(new SystemProcess(appName, packageName, icon, 0, 0, 0, totalBytes, totalBytes)); // pid, cpu, mem set to 0 as not relevant here
                }
            }

            // Sort by total data usage (descending)
            Collections.sort(dataHogs, new Comparator<SystemProcess>() {
                @Override
                public int compare(SystemProcess p1, SystemProcess p2) {
                    return Long.compare(p2.bytesSent + p2.bytesReceived, p1.bytesSent + p1.bytesReceived);
                }
            });

            // Return top 5
            return dataHogs.stream().limit(5).collect(Collectors.toList());

        } catch (Exception e) { // This will catch any other exceptions, including those from NetworkStatsManager
            Log.e(TAG, "Error fetching network usage stats: " + e.getMessage());
            runOnUiThread(() -> Toast.makeText(ReportsActivity.this, "Error fetching 24-hour network stats.", Toast.LENGTH_LONG).show());
        }
        return new ArrayList<>();
    }


    /**
     * CORE LOGIC: Fetches a list of all running user processes and their memory consumption.
     * UPDATED to use UsageStatsManager for a more comprehensive list of apps.
     */
    private List<SystemProcess> fetchRunningProcesses() {
        List<SystemProcess> processList = new ArrayList<>();
        PackageManager pm = getPackageManager();
        // ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE); // Not primarily used now

        long endTime = System.currentTimeMillis();
        // Query for the last 2 hours to get a more 'real-time' view of active apps
        long startTime = endTime - (2 * 60 * 60 * 1000); // Last 2 hours

        // Use INTERVAL_BEST for the most granular data available for the period
        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_BEST,
                startTime,
                endTime
        );

        if (usageStatsList == null || usageStatsList.isEmpty()) {
            Log.d(TAG, "UsageStatsManager returned null or empty list for INTERVAL_BEST (last 2 hours).");
            return processList;
        }

        // Define a threshold for what we consider 'recently active' (e.g., used in the last 10 minutes)
        long thresholdTimeForActive = endTime - (10 * 60 * 1000); // Last 10 minutes

        for (UsageStats usageStats : usageStatsList) {
            String packageName = usageStats.getPackageName();

            ApplicationInfo appInfo;
            try {
                appInfo = pm.getApplicationInfo(packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                // If app info not found, skip. Could be a removed app or obscure system component.
                Log.w(TAG, "AppInfo not found for package: " + packageName + ", skipping.");
                continue;
            }

            // Skip system apps and CyberX itself
            // Using ApplicationInfo flags for more robust system app detection
            if (((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ||
                    ((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) ||
                    packageName.equals(getPackageName())) {
                continue;
            }

            // Filter for genuinely 'active' apps within the queried interval (last 2 hours)
            // An app is considered active if it was used very recently OR had significant foreground time.
            if (usageStats.getLastTimeUsed() < thresholdTimeForActive && usageStats.getTotalTimeInForeground() < 1000) { // < 1 second foreground time
                continue; // Skip if not recently used and minimal foreground time
            }

            String appName = pm.getApplicationLabel(appInfo).toString();
            Drawable icon = pm.getApplicationIcon(appInfo);
            int uid = appInfo.uid;

            // Get memory usage (MOCK, as real accurate memory for other apps is highly restricted on non-rooted devices)
            long memoryKb = (long) (Math.random() * 100000) + 5000; // Mock memory usage (5MB - 105MB)

            // Get Network Usage (Bytes sent/received since boot - TrafficStats resets on device reboot)
            long bytesSent = TrafficStats.getUidTxBytes(uid);
            long bytesReceived = TrafficStats.getUidRxBytes(uid);

            // Get CPU Usage (MOCK, based on memory load, real-time per-app CPU is highly restricted)
            int mockCpu = (int) (Math.random() * 5); // 0-5% usage mock
            if (memoryKb > 50000) mockCpu = 15; // Set higher CPU mock for "larger" memory apps

            processList.add(new SystemProcess(appName, packageName, icon, 0, mockCpu, memoryKb, bytesSent, bytesReceived)); // PID is not easily available for UsageStats apps, set to 0
        }

        return processList;
    }

    /**
     * Categorizes a list of SystemProcess objects into currently active and background running apps.
     */
    private CategorizedProcesses categorizeProcesses(List<SystemProcess> allProcesses) {
        List<SystemProcess> activeApps = new ArrayList<>();
        List<SystemProcess> backgroundApps = new ArrayList<>();

        long currentTime = System.currentTimeMillis();
        long activeThreshold = currentTime - (5 * 60 * 1000); // Active if used in the last 5 minutes

        for (SystemProcess process : allProcesses) {
            // To check for true "active" state from UsageStats, we'd need the raw UsageStats objects here.
            // Since fetchRunningProcesses already filters for recent activity, we can use a simpler proxy.
            // For now, let's consider apps with some recent network activity or higher CPU mock as potentially "active".
            // A more robust solution might require passing original UsageStats objects through.

            // Using a simple proxy: apps with significant recent network activity or higher mock CPU
            if ((process.bytesSent + process.bytesReceived) > (1 * 1024 * 1024) || process.cpuUsagePercent > 10) { // >1MB data or >10% mock CPU
                activeApps.add(process);
            } else {
                backgroundApps.add(process);
            }
        }

        // As a simpler categorization, let's refine:
        // Apps with any non-zero bytes sent/received and used very recently (e.g., last 5 minutes in a more direct UsageStats query)
        // are good candidates for "active".
        // However, since `fetchRunningProcesses` already does an initial filter, a strict categorization is harder without raw UsageStats.
        // For this simplified split, the above logic is a heuristic.
        // A truly accurate split would require re-querying UsageStats with different intervals or adding `lastTimeUsed` to SystemProcess.

        // For now, let's use the current `activeApps` as those with higher network/cpu mock
        // and put the rest in `backgroundApps`.

        return new CategorizedProcesses(activeApps, backgroundApps);
    }

    // Helper class to return both lists
    private static class CategorizedProcesses {
        private final List<SystemProcess> activeApps;
        private final List<SystemProcess> backgroundApps;

        public CategorizedProcesses(List<SystemProcess> activeApps, List<SystemProcess> backgroundApps) {
            this.activeApps = activeApps;
            this.backgroundApps = backgroundApps;
        }

        public List<SystemProcess> getActiveApps() {
            return activeApps;
        }

        public List<SystemProcess> getBackgroundApps() {
            return backgroundApps;
        }
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

    /** NEW HELPER: Format bytes to KB, MB, GB (Similar to your Python tool's _format_bytes) */
    private String formatBytes(long bytes) {
        if (bytes <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));

        // Ensure index doesn't exceed array bounds
        if (digitGroups >= units.length) {
            digitGroups = units.length - 1;
        }

        return String.format(Locale.getDefault(), "%.2f %s", bytes / Math.pow(1024, digitGroups), units[digitGroups]);
    }
}