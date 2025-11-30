package com.dpsg.cyberx;

import android.graphics.drawable.Drawable;

// This model holds the MOST RECENT usage instance for an app
public class LastUsage {
    public String appName;
    public String packageName;
    public Drawable appIcon;
    public long lastStartTime;
    public long lastEndTime;

    public LastUsage(String appName, String packageName, Drawable appIcon, long lastStartTime, long lastEndTime) {
        this.appName = appName;
        this.packageName = packageName;
        this.appIcon = appIcon;
        this.lastStartTime = lastStartTime;
        this.lastEndTime = lastEndTime;
    }
}