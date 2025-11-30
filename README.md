# 📱 DPSG CyberX : Digital Privacy & Security Guardian (Android)

**Platform:** Android (Java / XML)  
**Package Name:** `com.dpsg.cyberx`  
**Status:** MVP Complete (Structural integrity achieved, API context needs final runtime tuning)

DPSG CyberX is a high-assurance, Android security application designed to give users complete visibility and proactive control over app permissions, resource usage, and background security risks. It acts as a **digital privacy guardian** by auditing apps that use sensitive system APIs.

---

## 🎯 Project Overview & Core Features

The application is structured into **four main functional modules**, all accessible from the central **MainActivity Dashboard**.

---

# I. Core Application Structure

### User-facing app/permission management features.

| Component | Function | Implementation Details |
|----------|----------|-------------------------|
| **Phase 1: App Permission Manager** | Lists installed apps and audits dangerous permissions (Camera, Mic, Storage, Location). Direct link to OS Settings provided. | Uses `PackageManager.getPackageInfo()` with `GET_PERMISSIONS` executed on a background thread. |
| **Phase 2: Real-Time Monitoring** | Tracks last usage time of sensitive hardware (Camera/Mic/Location) for each app over 7 days. | Powered by `UsageStatsManager` (foreground/background logs). |

---

# II. System Reporting & Security Auditing

### Background metrics collection and risk evaluation.

| Metric / Section | Function | Android API Used |
|------------------|----------|------------------|
| **Phase 3: Privacy Reports** | Generates system health summary: RAM/CPU load, Top 5 Network Hogs, DPSG Security Score. | `ActivityManager`, `Debug.MemoryInfo`, `TrafficStats` |
| **Phase 4: AI Behavioral Alerts** | Shows alerts triggered by rule-based analysis of CPU/RAM spikes or abnormal activity. | Internal Java logic (Mock data supported) |

---

# 📐 DPSG Security Score Logic (0–100%)

| Rule | Metric Checked | Deduction |
|------|----------------|-----------|
| **R1 (High CPU Hogs)** | App processes with CPU > 10% | –5 points per hog |
| **R2 (System Overload)** | Running processes > 50 | –10 points |
| **R3 (RAM Risk)** | RAM usage > 85% | –20 points |
| **R4 (Network Risk)** | Top network hog > 50MB | –15 points |

---

# 🚀 Getting Started

## Prerequisites
- Android Studio (Latest)
- Android SDK Platform 29+ (Android 10+)
- Java + XML support

---

## Installation

1. **Clone the Repository**
   ```
   git clone https://github.com/Skanthasishanth/Digital-Privacy-and-Security-Guardian---CyberX.git
   cd CyberX
   ```
## ▶️ Run Instructions

### **Open the Project**
- Open in **Android Studio**
- Allow **Gradle** to complete syncing

### **Run the App**
- Use a **Physical Android Device**  
  **OR**
- Use an **Android 10+ Emulator (API 29 or higher)**

---

## 🔐 Mandatory Runtime Permission (Critical)

Required for **Real-Time Monitoring** to function.

### Steps:
1. Open the app.
2. Navigate to the **Real-Time Monitoring** screen.
3. Tap **Grant Usage Access**.
4. In Android **Settings**, enable:
   **Permit usage access → CyberX**
5. Return to the app → The status becomes **Granted**.

---

## 💻 Expected Output

## **Dashboard**
A clean UI with the following 4 primary modules:
- **App Permissions**
- **Real-Time Monitoring**
- **Alerts**
- **Reports**

  
## 🖼️ Output Screenshot

![WhatsApp Image 2025-11-30 at 23 36 35_29144fd2](https://github.com/user-attachments/assets/e1881013-b319-4fae-af9d-8542dc5aa691)

---

## **App Permissions**
- Shows all installed apps  
- Selecting an app displays all **dangerous permissions**  
- A **Manage** button opens the OS settings for that app

  
## 🖼️ Output Screenshots

![WhatsApp Image 2025-11-30 at 23 36 35_6b3472f8](https://github.com/user-attachments/assets/a81a03f7-2121-4f82-b737-d1de94e5d203)

![WhatsApp Image 2025-11-30 at 23 36 35_33aca735](https://github.com/user-attachments/assets/3ecdf497-b251-4cc7-8b03-12b1b32a16e9)


---

## **Real Time Monitoring**
 - Shows which apps use sensitive permissions
 - 4 main sensitive permissions
 - Shows timing that apps which timing they used permissions


## 🖼️ Output Screenshots


![WhatsApp Image 2025-11-30 at 23 36 36_6ae9fb8b](https://github.com/user-attachments/assets/7486a31e-f895-42b1-a2e6-b4af20ad3808)

![WhatsApp Image 2025-11-30 at 23 36 36_59d9017d](https://github.com/user-attachments/assets/0d873393-366c-41c1-8dcf-e17b46aa90b3)
   

## **Reports**
Displays all major device security metrics:
- **Security Score**
- **RAM Usage**
- **CPU Usage**
- **Network Usage**
- **Top 5 Network/Data Hogs**
- **Memory-Sorted Process List**


## 🖼️ Output Screenshots

![WhatsApp Image 2025-11-30 at 23 36 37_fdb39f38](https://github.com/user-attachments/assets/4f2cb4ca-82c8-4e0d-91b1-a9dccaaa5c79)


![WhatsApp Image 2025-11-30 at 23 36 37_a6ac2b95](https://github.com/user-attachments/assets/f726003c-f3fb-438d-9105-a588373d5501)

---


## ***Alert for Based on Aehaviour***

   -   Shows crictical alerts
   -   Shows which apps using permission on night time
   -   Even screen locked

## 🖼️ Output Screenshot

![WhatsApp Image 2025-11-30 at 23 36 38_fd2d1bec](https://github.com/user-attachments/assets/6ae9f4a8-6924-426f-aa61-1d7dd6ecbe54)




## 🤝 Contribute

Contributions are welcome!

### Steps to Contribute:

1. **Fork this repository**

2. **Create a Feature Branch**
   ```
   git checkout -b feature/AmazingFeature
   ```
3. **Commit your changes**
   ```
     git commit -m "Add AmazingFeature"
   ```
4. **Push the branch**
   ```
      git push origin feature/AmazingFeature
   ```
5. **Open a Pull Request**

     Provide a clear explanation of what you changed and why.


## ⚖️ License

  Distributed under the MIT License.
  See LICENSE.md for more information.


## ✍️ Authors

**CyberX Development Team**

- **Perarasu M** — Founder & Lead Developer  
  *Android Development • Cybersecurity • System Monitoring Module*

- **Kantha Sishanth S** — Co-Founder & Co-Developer  
  *Feature Development • UI/UX • Debugging & Optimization*

If you like to contribute to this project, feel free to open a Pull Request!


