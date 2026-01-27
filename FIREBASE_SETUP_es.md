# Configuración de Firebase (Requerido)

Este proyecto utiliza Firebase para proporcionar:

- **Cloud Firestore** (ranking global y bote compartido)
- **Firebase Remote Config** (título y descripción del premio de forma remota)

El archivo `google-services.json` **no está incluido** en este repositorio.  
Cada usuario debe crear su propio proyecto Firebase y proporcionar su propia configuración.

Sigue los pasos a continuación para ejecutar la aplicación.

---

## 1. Crear un proyecto Firebase

1. Ve a: https://console.firebase.google.com
2. Pulsa **Crear un proyecto**
3. Sigue el asistente (las opciones por defecto son suficientes)

---

## 2. Añadir la app Android a Firebase

Dentro de tu proyecto Firebase:

1. Pulsa **Añadir app** → **Android**
2. Usa este nombre de paquete:

```
com.example.piedraPapelTijeras
```

3. Descarga el archivo generado:

```
google-services.json
```

4. Cópialo en esta carpeta del proyecto:

```
app/google-services.json
```

Importante:
- NO renombres el archivo.
- NO lo edites manualmente.

---

## 3. Activar Cloud Firestore

En Firebase Console:

1. Ve a **Build → Firestore Database**
2. Pulsa **Crear base de datos**
3. Selecciona **Modo de prueba** (para demostración)
4. Elige cualquier región

---

### Estructura de Firestore

La aplicación espera la siguiente estructura.

### Colección: `jugadores`

Los documentos se crean automáticamente desde la app.

Cada documento utiliza el nombre del jugador como ID y puede contener campos como:

- `nombre` (String)
- `puntuacion` (Number)
- `latitud` (Number, opcional)
- `longitud` (Number, opcional)

No es necesario crear manualmente esta colección.

---

### Documento: `configuracion/bote`

Crear manualmente:

Colección:
```
configuracion
```

Documento:
```
bote
```

Añadir campo:

```
puntos : Number (0)
```

Este documento almacena el bote compartido.

---

## 4. Activar Firebase Remote Config

En Firebase Console:

1. Ve a **Build → Remote Config**
2. Activa Remote Config
3. Añade los siguientes parámetros:

| Clave | Tipo | Valor de ejemplo |
|------|------|------------------|
| premio_titulo | String | Premio compartido |
| premio_descripcion | String | Gana partidas para conseguir el premio global |

Estos valores se cargan dinámicamente al iniciar la app.

---

## 5. Reglas de Firestore (Modo demo)

Para pruebas puedes usar reglas abiertas:

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

Estas reglas están pensadas SOLO para uso educativo o demostración.

---

## Listo

Una vez configurado Firebase:

1. Sincroniza Gradle
2. Ejecuta la app

El ranking, el bote compartido y la configuración remota del premio ya deberían funcionar.

---

## Navegación

- [Main README](README.md)
- [Documentación](README_es.md)
- [Dependencias](DEPENDENCIAS.md)
