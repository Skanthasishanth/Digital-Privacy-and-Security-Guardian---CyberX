package com.dpsg.cyberx;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.util.Log;
import android.widget.LinearLayout; // For the instruction guide
import java.util.ArrayList;
import java.util.List;

public class RealTimeMonitorActivity extends AppCompatActivity
        implements ResourceCategoryAdapter.OnCategoryClickListener { // Implement the interface

    private TextView permissionStatusText;
    private Button grantAccessButton;
    private LinearLayout instructionGuide; // Initialized field
    private RecyclerView recyclerView;

    // Constants for AppOpsManager fields
    public static final String EXTRA_OP_CODE = "extra_op_code";
    public static final String EXTRA_RESOURCE_NAME = "extra_resource_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_monitor);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Real-Time Monitoring");
        }

        permissionStatusText = findViewById(R.id.text_usage_permission_status);
        grantAccessButton = findViewById(R.id.btn_grant_usage_access);
        instructionGuide = findViewById(R.id.instruction_guide); // Initialize the instruction view
        recyclerView = findViewById(R.id.recycler_view_timeline);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Button directly launches the settings intent
        grantAccessButton.setOnClickListener(v -> launchUsageAccessSettings());

        loadResourceCategories();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check status every time the user returns to this screen
        checkUsageAccessStatus();
    }

    private void checkUsageAccessStatus() {
        if (hasUsageStatsPermission()) {
            permissionStatusText.setText("Usage Access Status: Granted (Monitoring Active)");
            permissionStatusText.setTextColor(getResources().getColor(R.color.design_default_color_secondary, getTheme())); // Green
            grantAccessButton.setVisibility(View.GONE);
            instructionGuide.setVisibility(View.GONE); // HIDE instructions if granted
        } else {
            permissionStatusText.setText("Usage Access Status: Required for Monitoring");
            permissionStatusText.setTextColor(getResources().getColor(R.color.design_default_color_error, getTheme())); // Red
            grantAccessButton.setVisibility(View.VISIBLE);
            instructionGuide.setVisibility(View.VISIBLE); // SHOW instructions if required
        }
    }

    private boolean hasUsageStatsPermission() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        if (appOps == null) {
            return false;
        }
        int mode = appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                getPackageName()
        );
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    /**
     * Launches the system intent that allows the user to grant Usage Access.
     */
    private void launchUsageAccessSettings() {
        try {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
            // Updated message to refer to the guide on screen
            Toast.makeText(this, "Redirecting to Settings. Follow the guide below!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Could not open Usage Access settings.", Toast.LENGTH_SHORT).show();
            Log.e("DPSG", "Failed to launch Usage Access settings.", e);
        }
    }

    /**
     * Loads the 4 main sensitive resource categories (Mock data and OpCodes).
     */
    private void loadResourceCategories() {
        // NOTE: AppOpsManager opcodes are critical for real monitoring
        List<ResourceCategory> categories = new ArrayList<>();
        // Note: ResourceCategory model and R.drawable icons are assumed to be available
        categories.add(new ResourceCategory("Microphone Access", AppOpsManager.OPSTR_RECORD_AUDIO, R.drawable.ic_mic, 45));
        categories.add(new ResourceCategory("Camera Access", AppOpsManager.OPSTR_CAMERA, R.drawable.ic_camera, 12));
        categories.add(new ResourceCategory("Location Access", AppOpsManager.OPSTR_FINE_LOCATION, R.drawable.ic_location, 98));
        categories.add(new ResourceCategory("Media/Storage Access", AppOpsManager.OPSTR_READ_EXTERNAL_STORAGE, R.drawable.ic_storage, 201));

        ResourceCategoryAdapter adapter = new ResourceCategoryAdapter(this, categories, this);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Handles click on a resource category, navigating to the detailed timeline.
     */
    @Override
    public void onCategoryClick(ResourceCategory category) {
        if (!hasUsageStatsPermission()) {
            Toast.makeText(this, "Grant Usage Access first!", Toast.LENGTH_SHORT).show();
            return;
        }
        // CRITICAL: Navigate to the UsageTimelineActivity, passing the specific resource OpCode
        Intent intent = new Intent(RealTimeMonitorActivity.this, UsageTimelineActivity.class);
        intent.putExtra(EXTRA_OP_CODE, category.opCode);
        intent.putExtra(EXTRA_RESOURCE_NAME, category.name);
        startActivity(intent);
    }
}