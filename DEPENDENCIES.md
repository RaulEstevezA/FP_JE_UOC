# Dependencies & Requirements

This document lists the technical requirements and external libraries needed to build and run the **Rock Paper Scissors** Android project.

---

## Development Environment

To work with this project you need:

- **Android Studio** (latest stable version recommended)
- **JDK 17** (or the version bundled with Android Studio)
- **Gradle** (managed automatically by Android Studio)
- Android SDK

Recommended:

- Android Emulator or physical Android device
- Git

---

## Minimum Android Version

The application targets modern Android devices.

Recommended:

- **Minimum SDK:** 26+
- **Target SDK:** Latest available

(Exact values can be checked in `build.gradle`.)

---

## Core Technologies

- Kotlin
- Jetpack Compose
- MVVM Architecture
- Coroutines & Flow

---

## Local Persistence

- Room (SQLite)
  - room-runtime
  - room-ktx
  - kapt / ksp for Room compiler

Used for local storage of player data and scores.

---

## Firebase Services

The project uses the following Firebase components:

- Firebase Authentication
- Cloud Firestore
- Firebase Remote Config

These are configured via:

```
app/google-services.json
```

See Firebase setup guide:

- [FIREBASE_SETUP_en.md](FIREBASE_SETUP_en.md)

---

## Networking & Serialization

- Retrofit
- Moshi
- Moshi Kotlin Adapter

Used for network communication and JSON parsing.

---

## Media & Device Services

- MediaPlayer (background music)
- Android Location Services (FusedLocationProviderClient)
- CalendarContract + ContentResolver (calendar integration)

---

## Permissions

The app may request the following permissions at runtime:

- Location access (ACCESS_FINE_LOCATION / ACCESS_COARSE_LOCATION)
- Calendar access (READ / WRITE CALENDAR)

These are required for optional features.

---

## How Dependencies Are Managed

All dependencies are declared in Gradle:

- `build.gradle`
- `libs.versions.toml` (if present)

Android Studio will automatically download everything during project sync.

---

## Navigation

- Main README: [README.md](README.md)
- Extended documentation: [README_en.md](README_en.md)
- Firebase setup: [FIREBASE_SETUP_en.md](FIREBASE_SETUP_en.md)
