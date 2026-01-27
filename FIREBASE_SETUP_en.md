# Firebase Setup (Required)

This project uses Firebase to provide:

- **Cloud Firestore** (global ranking and shared prize pool)
- **Firebase Remote Config** (remote prize title and description)

The file `google-services.json` is **not included** in this repository.  
Each user must create their own Firebase project and provide their own configuration.

Follow the steps below to run the app.

---

## 1. Create a Firebase Project

1. Go to: https://console.firebase.google.com
2. Click **Create a project**
3. Follow the wizard (default options are fine)

---

## 2. Add Android App to Firebase

Inside your Firebase project:

1. Click **Add app** → **Android**
2. Use this package name:

```
com.example.piedraPapelTijeras
```

3. Download the generated file:

```
google-services.json
```

4. Copy it into this folder in the project:

```
app/google-services.json
```

Important:
- Do NOT rename the file.
- Do NOT edit it manually.

---

## 3. Enable Cloud Firestore

In Firebase Console:

1. Go to **Build → Firestore Database**
2. Click **Create database**
3. Select **Test mode** (for demo purposes)
4. Choose any region

---

### Firestore Structure

The app expects the following structure.

### Collection: `jugadores`

Documents are created automatically by the app.

Each document uses the player name as ID and may contain fields such as:

- `nombre` (String)
- `puntuacion` (Number)
- `latitud` (Number, optional)
- `longitud` (Number, optional)

No manual creation is required for this collection.

---

### Document: `configuracion/bote`

Create manually:

Collection:
```
configuracion
```

Document:
```
bote
```

Add field:

```
puntos : Number (0)
```

This document stores the shared prize pool.

---

## 4. Enable Firebase Remote Config

In Firebase Console:

1. Go to **Build → Remote Config**
2. Enable Remote Config
3. Add the following parameters:

| Key | Type | Example value |
|-----|------|---------------|
| premio_titulo | String | Shared Prize |
| premio_descripcion | String | Win games to earn the global prize |

These values are loaded dynamically by the app at startup.

---

## 5. Firestore Rules (Demo Mode)

For testing purposes you may use open rules:

```js
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if true;
    }
  }
}
```

These rules are intended ONLY for demo or educational use.

---

## Done

Once Firebase is configured:

1. Sync Gradle
2. Run the app

The ranking, shared prize pool and remote prize configuration should now work.

---

## Navigation

- [Main README](README.md)
- [English documentation](README_en.md)
- [Dependencies](DEPENDENCIES.md)
