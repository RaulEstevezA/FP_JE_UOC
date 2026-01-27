# Dependencias y Requisitos

Este documento enumera los requisitos técnicos y las librerías externas necesarias para compilar y ejecutar el proyecto Android **Piedra Papel Tijeras**.

---

## Entorno de Desarrollo

Para trabajar con este proyecto necesitas:

- **Android Studio** (última versión estable recomendada)
- **JDK 17** (o la versión incluida con Android Studio)
- **Gradle** (gestionado automáticamente por Android Studio)
- Android SDK

Recomendado:

- Emulador Android o dispositivo físico
- Git

---

## Versión mínima de Android

La aplicación está orientada a dispositivos Android modernos.

Recomendado:

- **SDK mínimo:** 26+
- **SDK objetivo:** Última versión disponible

(Los valores exactos pueden consultarse en `build.gradle`).

---

## Tecnologías Principales

- Kotlin
- Jetpack Compose
- Arquitectura MVVM
- Corrutinas y Flow

---

## Persistencia Local

- Room (SQLite)
  - room-runtime
  - room-ktx
  - kapt / ksp para el compilador de Room

Se utiliza para almacenar datos locales del jugador y puntuaciones.

---

## Servicios Firebase

El proyecto utiliza los siguientes componentes de Firebase:

- Firebase Authentication
- Cloud Firestore
- Firebase Remote Config

Estos se configuran mediante:

```
app/google-services.json
```

Consulta la guía de configuración:

- [FIREBASE_SETUP_es.md](FIREBASE_SETUP_es.md)

---

## Red y Serialización

- Retrofit
- Moshi
- Moshi Kotlin Adapter

Se usan para comunicación de red y parseo de JSON.

---

## Multimedia y Servicios del Dispositivo

- MediaPlayer (música de fondo)
- Servicios de ubicación Android (FusedLocationProviderClient)
- CalendarContract + ContentResolver (integración con calendario)

---

## Permisos

La aplicación puede solicitar los siguientes permisos en tiempo de ejecución:

- Acceso a ubicación (ACCESS_FINE_LOCATION / ACCESS_COARSE_LOCATION)
- Acceso al calendario (READ / WRITE CALENDAR)

Estos permisos son necesarios para funcionalidades opcionales.

---

## Gestión de Dependencias

Todas las dependencias están declaradas en Gradle:

- `build.gradle`
- `libs.versions.toml` (si está presente)

Android Studio descargará automáticamente todo durante la sincronización del proyecto.

---

## Navegación

- README principal: [README.md](README.md)
- Documentación extendida: [README_es.md](README_es.md)
- Configuración Firebase: [FIREBASE_SETUP_es.md](FIREBASE_SETUP_es.md)
