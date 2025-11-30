package com.dpsg.cyberx;

import android.graphics.drawable.Drawable;
import java.io.Serializable; // <-- Import Serializable

// Implement Serializable so we can pass this object to another Activity
public class InstalledApp implements Serializable {
    private String name;
    private String packageName;
    private Drawable icon;
    private int grantedPermissionsCount; // Number of dangerous permissions granted

    // Constructor
    public InstalledApp(String name, String packageName, Drawable icon, int grantedPermissionsCount) {
        this.name = name;
        this.packageName = packageName;
        this.icon = icon;
        this.grantedPermissionsCount = grantedPermissionsCount;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public int getGrantedPermissionsCount() {
        return grantedPermissionsCount;
    }

    // IMPORTANT: When passing the icon (Drawable) in a real app, you must convert it to a
    // serializable format (like a byte array). For simplicity in this tutorial, we will only
    // pass the package name when navigating to the detail screen, avoiding complex serialization for now.
}