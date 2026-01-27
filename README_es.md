# Piedra Papel Tijeras — Documentación Extendida

<p align="center">
  <img src="images/app_preview.png" alt="Vista general del juego" width="650">
</p>

Este documento proporciona la documentación técnica y funcional extendida de la aplicación Android **Piedra Papel Tijeras**, desarrollada por el equipo **Kotlin Kaos** como proyecto educativo para la asignatura *Desarrollo de aplicaciones móviles nativas*.

El objetivo del proyecto fue construir progresivamente un juego móvil utilizando tecnologías modernas de Android e integrando servicios en la nube y funcionalidades del dispositivo.

---

## Descripción del Proyecto

Piedra Papel Tijeras es un juego clásico mejorado con capacidades móviles actuales:

- Autenticación de usuarios
- Ranking global en tiempo real
- Bote compartido (premio global)
- Persistencia local
- Soporte multidioma
- Música de fondo
- Localización del jugador
- Registro de victorias en el calendario del dispositivo
- Configuración remota de los textos del premio

La aplicación se desarrolló de forma incremental a través de varios productos, aumentando la complejidad en cada fase.

---

## Arquitectura

El proyecto sigue una arquitectura **MVVM (Model–View–ViewModel)**:

- **Model**: Clases de datos y repositorios (entidades Room, modelos Firebase)
- **View**: Interfaces construidas con Jetpack Compose
- **ViewModel**: Lógica de negocio y gestión del estado mediante StateFlow

Tecnologías principales:

- Kotlin
- Jetpack Compose
- Coroutines & Flow
- Room (SQLite)
- Firebase Authentication
- Cloud Firestore
- Firebase Remote Config

Esta separación mejora el mantenimiento, las pruebas y la escalabilidad.

---

## Base de Datos Local (Room / SQLite)

Room se utiliza para la persistencia local:

- Email o nombre del jugador
- Puntuaciones locales
- Estado de la partida

Esto permite continuar el progreso incluso sin conexión a internet.

Componentes clave:

- Entidad: `Jugador`
- DAO: `JugadorDao`
- Base de datos: `JugadoresDatabase`
- Capa de repositorio

---

## Integración con Firebase

Firebase proporciona la funcionalidad en la nube:

### Cloud Firestore

Se utiliza para:

- Ranking global (colección `jugadores`)
- Bote compartido (documento `configuracion/bote`)

Características:

- Escucha en tiempo real para actualizaciones del ranking
- Transacciones para actualizar el bote de forma segura

### Firebase Remote Config

Se utiliza para definir remotamente:

- Título del premio (`premio_titulo`)
- Descripción del premio (`premio_descripcion`)

Estos valores se cargan dinámicamente al iniciar la aplicación.

### Firebase Authentication

Se utiliza para identificar a los usuarios y asociar las puntuaciones de forma segura.

Las instrucciones completas están disponibles aquí:

- [Configuración de Firebase (Español)](FIREBASE_SETUP_es.md)

---

## Interfaz de Usuario

Toda la interfaz está implementada con **Jetpack Compose**:

- Componentes declarativos (`Column`, `Row`, `Box`, `Text`, `Button`)
- Estado reactivo mediante StateFlow
- Previsualización durante el desarrollo

No se utilizan layouts XML.

---

## Funcionalidades del Juego

### Jugabilidad

- Lógica clásica de Piedra–Papel–Tijera
- Elección aleatoria del oponente
- Cálculo de puntuación por ronda

### Ranking

- Visualización de los mejores jugadores en tiempo real
- Ordenado por puntuación descendente
- Actualización automática mediante listeners de Firestore

### Bote Compartido

- Los puntos se suman a un bote global
- Transacciones atómicas para evitar conflictos
- Los jugadores pueden reclamar el bote cuando se cumplen las condiciones

---

## Audio

La música de fondo se implementa con `MediaPlayer`:

- Se inicia automáticamente
- Libera recursos correctamente
- Usa Audio Focus para pausar o reanudar al salir o volver a la app

---

## Localización

La aplicación obtiene la ubicación del jugador usando:

- `FusedLocationProviderClient`
- Corrutinas (`suspend` functions)

La latitud y longitud se almacenan opcionalmente en Firestore para cada jugador.

---

## Integración con Calendario

Las victorias pueden registrarse en el calendario del dispositivo:

- Se solicitan permisos en tiempo de ejecución
- Los eventos se insertan usando `CalendarContract` y `ContentResolver`
- Incluyen fecha y descripción de la victoria

---

## Internacionalización

La app soporta varios idiomas:

- Inglés
- Español

Implementación:

- Sin textos hardcodeados
- Archivos `strings.xml` en `values` y `values-es`
- Textos cargados mediante `stringResource()`

---

## Estructura del Proyecto (Simplificada)

```
app/
 ├── data/
 │   ├── dao/
 │   ├── model/
 │   ├── network/
 │   └── source/
 ├── repositorio/
 ├── viewmodel/
 └── ui/
```

---

## Propósito Educativo

Este repositorio está destinado exclusivamente a fines educativos.

Demuestra:

- Buenas prácticas modernas en Android
- Arquitectura MVVM
- Integración con servicios en la nube
- Uso de servicios del dispositivo
- Trabajo colaborativo en equipo

---

## Equipo

Desarrollado por **Kotlin Kaos**:

- Gerard Melich  
- Jorge Gongora  
- Miguel Garzo  
- Raul Estevez  
- Xavier Pujol  

---

## Navegación

- README principal: [README.md](README.md)
- Configuración Firebase: [FIREBASE_SETUP_es.md](FIREBASE_SETUP_es.md)
- Dependencias: [DEPENDENCIAS.md](DEPENDENCIAS.md)
