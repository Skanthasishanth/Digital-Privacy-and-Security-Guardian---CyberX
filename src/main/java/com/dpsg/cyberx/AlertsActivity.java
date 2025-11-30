package com.dpsg.cyberx;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Locale;

public class AlertsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    // Model for a single alert
    public static class Alert {
        public String title;
        public String description;
        public String severity; // "HIGH", "MEDIUM", "LOW"
        public long timestamp;

        public Alert(String title, String description, String severity, long timestamp) {
            this.title = title;
            this.description = description;
            this.severity = severity;
            this.timestamp = timestamp;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("AI Behavioral Alerts");
        }

        recyclerView = findViewById(R.id.recycler_view_alerts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load mock alerts based on system analysis
        List<Alert> alerts = createMockAlerts();
        AlertsAdapter adapter = new AlertsAdapter(this, alerts);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Generates mock rule-based alerts based on hypothetical system analysis.
     */
    private List<Alert> createMockAlerts() {
        List<Alert> alerts = new ArrayList<>();
        long now = System.currentTimeMillis();

        // 1. High Severity Alert: Microphone used unexpectedly (Rule: Mic usage between 1 AM - 5 AM)
        alerts.add(new Alert(
                "Suspicious Mic Access (HIGH)",
                "System recorded an unknown app accessing the microphone at 3:15 AM while the screen was locked.",
                "HIGH",
                now - TimeUnit.HOURS.toMillis(4)
        ));

        // 2. Medium Severity Alert: High resource consumption (Rule: CPU > 15% by a background process)
        alerts.add(new Alert(
                "High Background Resource Use",
                "Third-party process 'com.gaming.service' used 25% CPU for over 10 minutes in the background.",
                "MEDIUM",
                now - TimeUnit.MINUTES.toMillis(30)
        ));

        // 3. High Severity Alert: Location abuse
        alerts.add(new Alert(
                "Persistent Location Tracking (HIGH)",
                "Location was accessed 12 times in 5 minutes by a social media app.",
                "HIGH",
                now - TimeUnit.HOURS.toMillis(1)
        ));

        // 4. Low Severity Alert: Excessive permission count (Rule: App has 10+ dangerous permissions)
        alerts.add(new Alert(
                "Review App Permissions",
                "Calculator App (mock) has 15 granted dangerous permissions. Consider auditing.",
                "LOW",
                now - TimeUnit.DAYS.toMillis(2)
        ));

        // 5. Medium Severity Alert: Storage access warning
        alerts.add(new Alert(
                "Mass Storage Read",
                "A file manager scanned 5GB of external storage media in one session.",
                "MEDIUM",
                now - TimeUnit.HOURS.toMillis(8)
        ));

        return alerts;
    }
}