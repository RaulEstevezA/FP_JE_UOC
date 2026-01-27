# Rock Paper Scissors (Android)

<p align="center">
  <img src="images/app_preview.png" alt="Game overview" width="600">
</p>

**Rock Paper Scissors** is an Android application developed in **Kotlin using Jetpack Compose**.  
It implements the classic game while adding advanced features such as global ranking, shared prize pool, multi-language support, music, location services, and data persistence.

This repository is presented as an **educational project**, developed by the **Kotlin Kaos** team as part of the course *Native Mobile Application Development*.

---

## Documentation

Extended documentation is available in both languages:

- 🇬🇧 [**English documentation**](README_en.md)

- 🇪🇸 [**Documentación en español**](README_es.md)  

---

## Dependencies & Requirements

Technical requirements and external libraries are documented separately:

- 🇬🇧 [**Dependencies (English)**](DEPENDENCIES.md)
- 🇬🇧 [**Firebase setup (required to run the app)**](FIREBASE_SETUP_en.md)

- 🇪🇸 [**Dependencias (Español)**](DEPENDENCIAS.md)
- 🇪🇸 [**Firebase setup (requisito para poder usar la app)**](FIREBASE_SETUP_es.md)

---

## Technologies

- Kotlin
- Jetpack Compose
- MVVM architecture
- Room (SQLite local database)
- Firebase Authentication
- Cloud Firestore
- Firebase Remote Config
- Coroutines & Flow
- MediaPlayer (audio)
- Android Location Services
- CalendarContract (event insertion)

---

## Features

- Rock–Paper–Scissors game
- Real-time global ranking
- Shared prize pool (global pot)
- User authentication
- Local game persistence
- Multi-language support
- Background music
- Victory registration in device calendar
- Player location retrieval
- Remote prize configuration

---

## Team

This project has been developed by the **Kotlin Kaos** team:

- **Gerard Melich** [GitHub](https://github.com/GerardMeR) | [LinkedIn](https://www.linkedin.com/in/gerard-melich-rallo-2a2780316/)
- **Jorge Gongora** [GitHub](https://github.com/JorgeGonSan) | [LinkedIn](https://www.linkedin.com/in/jorge-góngora-sánchez-361597b8/)
- **Miguel Garzo** [GitHub](https://github.com/MiguelGarzo) | [LinkedIn](https://www.linkedin.com/in/miguel-garzo-moreno-6b1829182/)
- **Raul Estevez** [GitHub](https://github.com/RaulEstevezA) | [LinkedIn](https://www.linkedin.com/in/raulesteveza/) 
- **Xavier Pujol** [GitHub](https://github.com/xpujolt-coder) | [LinkedIn](https://www.linkedin.com/in/xaviertenedor/)

---

## License

This project is intended for educational purposes only.

Distributed under the Creative Commons Attribution–NonCommercial–NoDerivatives 4.0 International License.  
See the [LICENSE](LICENSE) file for more details.

---

## Notes

- This project requires Firebase configuration to run properly.
- The `google-services.json` file is NOT included in the repository.
- Each user must create their own Firebase project:
  - 🇬🇧 See [FIREBASE_SETUP_en.md](FIREBASE_SETUP_en.md)
  - 🇪🇸 Ver [FIREBASE_SETUP_es.md](FIREBASE_SETUP_es.md)
- This README provides only a general overview.
- Detailed explanations, screenshots, and full workflow are available in the extended documentation.