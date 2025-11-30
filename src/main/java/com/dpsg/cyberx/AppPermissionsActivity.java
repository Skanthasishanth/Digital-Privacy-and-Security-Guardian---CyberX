package com.dpsg.cyberx;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent; // Needed for navigation to detail screen
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppPermissionsActivity extends AppCompatActivity {

    private static final String TAG = "AppPermissionsActivity";
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView totalAppsText;

    // NEW FIELD: Executor for running background tasks
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_permissions);

        // Initialize Executor
        executorService = Executors.newSingleThreadExecutor();

        // Set up the Action Bar/Toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("App Permissions Manager");
        }

        // Initialize UI components
        recyclerView = findViewById(R.id.recycler_view_apps);
        progressBar = findViewById(R.id.progress_bar);
        totalAppsText = findViewById(R.id.text_total_apps);

        // Configure RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Start loading the app list in the background
        loadInstalledApps();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // CRITICAL: Shut down the executor when the Activity is destroyed to prevent memory leaks
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }


    /**
     * Retrieves the list of installed applications using Android's PackageManager,
     * running the heavy I/O work on a background thread.
     */
    private void loadInstalledApps() {
        // Show loading state immediately on the UI thread
        progressBar.setVisibility(View.VISIBLE);
        totalAppsText.setVisibility(View.GONE);

        // Submit the heavy work to the background thread
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                List<InstalledApp> appList = new ArrayList<>();
                PackageManager packageManager = getPackageManager();

                // --- HEAVY WORK: This part takes time and MUST run off the main thread ---
                List<ApplicationInfo> packages = packageManager.getInstalledApplications(PackageManager.MATCH_UNINSTALLED_PACKAGES);

                for (ApplicationInfo packageInfo : packages) {
                    if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        continue;
                    }

                    try {
                        String appName = packageManager.getApplicationLabel(packageInfo).toString();
                        String packageName = packageInfo.packageName;
                        android.graphics.drawable.Drawable icon = packageManager.getApplicationIcon(packageInfo);

                        // 2. Determine Permissions (Mock data as before)
                        int mockPermissionCount = appName.length() % 5 + 5;

                        InstalledApp app = new InstalledApp(appName, packageName, icon, mockPermissionCount);
                        appList.add(app);

                    } catch (Exception e) {
                        Log.e(TAG, "Failed to load details for package: " + packageInfo.packageName + " Error: " + e.getMessage());
                    }
                }

                // --- SWITCH BACK TO THE UI THREAD TO UPDATE THE VIEW ---
                // UI changes must happen on the Main Thread via runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 3. Update the UI
                        progressBar.setVisibility(View.GONE);
                        totalAppsText.setText("Total Apps Scanned: " + appList.size());
                        totalAppsText.setVisibility(View.VISIBLE);

                        // Set the adapter to display the data
                        AppListAdapter adapter = new AppListAdapter(AppPermissionsActivity.this, appList, new AppListAdapter.OnAppClickListener() {
                            @Override
                            public void onAppClick(InstalledApp app) {
                                // Phase 3 Logic: Navigate to AppDetailActivity, passing the package name
                                Intent detailIntent = new Intent(AppPermissionsActivity.this, AppDetailActivity.class);
                                detailIntent.putExtra(AppDetailActivity.EXTRA_PACKAGE_NAME, app.getPackageName());
                                startActivity(detailIntent);
                            }
                        });
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        });
    }
}