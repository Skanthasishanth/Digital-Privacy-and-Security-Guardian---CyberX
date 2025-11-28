# 📱 Digital Privacy & Security Guardian (DPSG)

### AI-Powered Real-Time Privacy Protection for Android Devices

## 🔐 Overview

The Digital Privacy & Security Guardian (DPSG) is an advanced, AI-powered mobile security application engineered to give users complete real-time control over their digital privacy. Unlike traditional antivirus tools that operate reactively, DPSG adopts a proactive, behavioral monitoring approach, focusing on preventing unauthorized data access, background sensor abuse, and third-party tracking.


DPSG brings together OS-level monitoring, Light Machine Learning (ML) anomaly detection, built-in VPN security, and a Unified Permission Dashboard — creating a single, intelligent, privacy-first security system for modern Android devices.

## ✨ Key Features

### 🔎 Real-Time Sensor Monitoring

Tracks Camera, Microphone, and Location usage in real time.

Identifies which app is accessing sensitive sensors live.

Logs new device connections and network events.

### 🧠 AI & Intelligence Layer

Rule-Based Detection for known risky behaviors.

Light ML Anomaly Detection to flag unusual sensor or network activity.

Optimized to maintain <5% False Positive Rate (FPR).

### 🛡️ Integrated VPN + Ad Blocking

High-speed, encrypted VPN (WireGuard/OpenVPN supported).

DNS-based policy filtering to block trackers and ad networks.

Prevents hidden DNS lookups used for tracking.

### 🖥️ Unified Dashboard

Centralized control of all app permissions.

Simplifies Android’s complex permission menus.

One-click toggles for VPN, monitoring, and sensor status.

### 📊 Weekly Privacy & Security Reports

Automatically generated.

Highlights sensor usage patterns, suspicious activity, and recommendations.

Designed to boost digital awareness and literacy.

### 🌐 Basic IoT Device Scanning

Detects new devices connected to the same Wi-Fi network.

Helps users identify unknown IoT connections.

## 🧭 System Architecture

### DPSG follows a modular, scalable architecture with four core layers:

#### 1. Monitoring Layer

Tracks sensor access

Records network activity

Performs lightweight OS-level background operations

#### 2. Network Layer

VPN tunneling

DNS-based ad/tracker blocking

Secure network traffic encryption

#### 3. Intelligence Layer (AI)

Rule-based threat detection

ML-based anomaly detection

Behavioral scoring engine

#### 4. Presentation Layer

Unified Dashboard

Weekly Report Interface

Visualization of sensor & network activity


<img width="1200" height="700" alt="system_architecture" src="https://github.com/user-attachments/assets/4572a475-3349-41c5-a3f2-030b23914bfc" />


## 📝 Problem Statement

Users today face growing digital privacy challenges:

Excessive and opaque app permission usage

Background access to Camera/Mic/Location without user awareness

Third-party tracking via hidden DNS lookups

Fragmented privacy controls across the OS

Traditional antivirus solutions detect threats after compromise

Lack of proactive behavioral monitoring

DPSG solves this by providing visibility, intelligence, and control — all in one place.

## 🎯 Project Scope

#### The initial release focuses on:

Android platform

Full implementation of real-time tracking & rule-based detection

Integrated VPN + DNS filtering

Weekly reporting

Unified UI for permissions

Scalable architecture for future ML expansion

Low resource (battery/CPU) usage

## ⚙️ Functional Requirements

Real-time monitoring of Camera/Mic/Location

Live alerts for unusual activity

Unified Permission Dashboard

Weekly auto-generated reports

One-click VPN & Ad-Blocker

Basic IoT device detection

Secure local storage of logs and reports

## 🛠️ Non-Functional Requirements

Requirement	Target

Battery Performance	Lightweight background operations

Accuracy (ML)	FPR < 5%

Security	Encrypted storage, GDPR compliance

Reliability	99.9% uptime for monitoring service

Maintainability	Modular, well-documented architecture

Usability	Minimal clicks, simplified UI


## 📐 System Analysis & Design

#### DPSG is designed to address:

Fragmented privacy controls

Lack of real-time behavioral detection

Growing need for AI-driven privacy intelligence

Unified & simplified user experience

The architecture supports expansion into:

More advanced ML models

Cross-device IoT monitoring

Advanced threat intelligence

#### DFD Level 0:

<img width="1200" height="700" alt="dfd_level0" src="https://github.com/user-attachments/assets/f3fe474f-c14f-46ea-b925-0fe6c985aef2" />


#### DFD Level 1:

<img width="1200" height="700" alt="dfd_level1" src="https://github.com/user-attachments/assets/cca7db98-2412-45b2-92aa-e5b4daea5a3e" />


## 🧑‍💼 Use Case Diagram

<img width="1200" height="700" alt="usecase_diagram" src="https://github.com/user-attachments/assets/85ac73a1-39ee-4b13-a7ef-d8a78fb11ff4" />


## 📚 Literature Support

#### The system is backed by research in:

ML-based runtime permission monitoring

DNS-based tracking prevention via VPN

Privacy-enhancing technologies (PETs) in mobile OS

User-centric design for boosting digital literacy

## 🧾 References

Sharma, A., & Gupta, S. (2023). Machine Learning for Runtime Permission Monitoring in Android Applications. International Journal of Computer Applications.

Chen, M., & Kumar, R. (2022). DNS-based Tracking Prevention in Mobile VPN Architectures. International Journal of Emerging Technologies.

Li, Y., & Das, K. (2022). Privacy-Enhancing Technologies in Mobile Operating Systems. IEEE Access.

Android Developer Documentation — developer.android.com

## 🧑‍💻 Tech Stack (Planned / Implemented)

Android (Java/Kotlin)

Python ML Models (For anomaly detection – optional future phase)

WireGuard / OpenVPN for VPN integration

Local encrypted storage (SQLCipher/Room DB)

Custom DNS filtering engine

## 🚀 Future Enhancements

Full ML-based behavioral modeling

Network traffic classification (AI-powered)

Integration with IoT ecosystem monitoring

Cloud-based encrypted backups

Real-time device threat scoring

## 📄 License

This project can be made open-source under the MIT License or the preferred institutional license.

## 🤝 Contributors

KANTHA SISHANTH S (212222100020)

Department of Computer Science & Engineering (Cyber Security)

Saveetha Engineering College
