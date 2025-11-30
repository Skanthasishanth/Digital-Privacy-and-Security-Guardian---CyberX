package com.dpsg.cyberx;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PACKAGE_NAME = "extra_package_name";
    private static final String TAG = "AppDetailActivity";

    private RecyclerView recyclerView;
    private TextView appNameText;
    private TextView packageText;
    private TextView summaryText;
    private ProgressBar progressBar;
    private ExecutorService executorService;
    private String appPackageName;

    // UPDATED: List of common dangerous permissions to filter and show, including media access
    private static final String[] DANGEROUS_PERMISSIONS = {
            "android.permission.CAMERA",
            "android.permission.RECORD_AUDIO",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.READ_CONTACTS",
            "android.permission.READ_SMS",
            // --- Sensitive Media Permissions ---
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.READ_MEDIA_IMAGES", // For Photos (Android 13+)
            "android.permission.READ_MEDIA_VIDEO", // For Videos (Android 13+)
            "android.permission.READ_MEDIA_AUDIO", // For Audio files (Android 13+)
            "android.permission.WRITE_EXTERNAL_STORAGE" // Legacy write access
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);

        // 1. Get package name from the Intent
        appPackageName = getIntent().getStringExtra(EXTRA_PACKAGE_NAME);

        // 2. Initialize UI
        appNameText = findViewById(R.id.text_detail_app_name);
        packageText = findViewById(R.id.text_detail_package_name);
        summaryText = findViewById(R.id.text_permission_summary);
        recyclerView = findViewById(R.id.recycler_view_permissions);
        progressBar = findViewById(R.id.detail_progress_bar);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 3. Setup Executor and load data
        executorService = Executors.newSingleThreadExecutor();
        loadAppDetails();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("App Details");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }

    private void loadAppDetails() {
        progressBar.setVisibility(View.VISIBLE);

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final List<String> grantedPermissionsList = new ArrayList<>();
                int grantedDangerousCount = 0;
                PackageManager pm = getPackageManager();

                try {
                    // Get ALL requested and granted permissions for the package
                    PackageInfo packageInfo = pm.getPackageInfo(
                            appPackageName,
                            PackageManager.GET_PERMISSIONS | PackageManager.GET_META_DATA
                    );

                    // Set basic app info on the UI thread
                    final String appName = pm.getApplicationLabel(packageInfo.applicationInfo).toString();
                    runOnUiThread(() -> {
                        appNameText.setText(appName);
                        packageText.setText(appPackageName);
                    });

                    // 4. CORE LOGIC: Check each requested permission
                    if (packageInfo.requestedPermissions != null) {
                        for (int i = 0; i < packageInfo.requestedPermissions.length; i++) {
                            String permission = packageInfo.requestedPermissions[i];
                            int result = packageInfo.requestedPermissionsFlags[i];

                            // Check if the permission is granted (the flag is set)
                            boolean isGranted = (result & PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0;

                            // We only care about granted permissions that are in our "dangerous" list for this view
                            if (isGranted && isDangerousPermission(permission)) {
                                grantedPermissionsList.add(permission);
                                grantedDangerousCount++;
                            }
                        }
                    }

                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(TAG, "Package not found: " + appPackageName, e);
                    Toast.makeText(AppDetailActivity.this, "Error: App not found.", Toast.LENGTH_LONG).show();
                    return;
                }

                final int finalCount = grantedDangerousCount;

                // Update UI with results
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    summaryText.setText("Granted Dangerous Permissions: " + finalCount);

                    // Setup Permission List Adapter
                    PermissionDetailAdapter adapter = new PermissionDetailAdapter(
                            AppDetailActivity.this,
                            grantedPermissionsList,
                            AppDetailActivity.this::onManagePermissionClick
                    );
                    recyclerView.setAdapter(adapter);
                });
            }
        });
    }

    // Helper to filter for the permissions we want to display
    private boolean isDangerousPermission(String permission) {
        for (String dangerous : DANGEROUS_PERMISSIONS) {
            if (dangerous.equals(permission)) {
                return true;
            }
        }
        return false;
    }

    // 5. Revoke/Manage Permission Click Handler
    private void onManagePermissionClick(String permissionName) {
        // This is the core action: direct the user to the OS settings for this app.
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", appPackageName, null);
            intent.setData(uri);
            startActivity(intent);
            Toast.makeText(this, "Opening App Settings to manage permissions...", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Could not open settings.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Failed to open settings: ", e);
        }
    }
}