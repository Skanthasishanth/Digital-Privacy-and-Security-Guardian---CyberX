package com.dpsg.cyberx;

import android.graphics.drawable.Drawable;

public class SystemProcess {
    public String name;
    public String packageName;
    public Drawable icon;
    public int pid; // Process ID
    public int cpuUsagePercent; // Mock/Calculated CPU usage
    public long memoryUsageKb; // Memory used in Kilobytes

    // NEW FIELDS for Network Audit
    public long bytesSent;
    public long bytesReceived;

    public SystemProcess(String name, String packageName, Drawable icon, int pid, int cpuUsagePercent, long memoryUsageKb, long bytesSent, long bytesReceived) {
        this.name = name;
        this.packageName = packageName;
        this.icon = icon;
        this.pid = pid;
        this.cpuUsagePercent = cpuUsagePercent;
        this.memoryUsageKb = memoryUsageKb;
        this.bytesSent = bytesSent;
        this.bytesReceived = bytesReceived;
    }
}