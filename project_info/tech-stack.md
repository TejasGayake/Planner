# Tech Stack — Job Tracker Android App

## Language & Runtime

| Property | Value |
|---|---|
| Primary language | Kotlin 1.9.22 |
| JVM target | JVM 17 |
| Min SDK | 26 (Android 8.0 Oreo) |
| Target SDK | 34 (Android 14) |
| Compile SDK | 34 |

## Frameworks & Libraries

### Core
| Library | Version | Purpose |
|---|---|---|
| Jetpack Compose BOM | 2024.02.00 | UI toolkit |
| Material 3 | via BOM | Material Design 3 components |
| Navigation Compose | 2.7.7 | Screen navigation |
| Lifecycle (ViewModel) | 2.7.0 | ViewModel + StateFlow |
| Activity Compose | 1.8.2 | Activity integration |

### Database
| Library | Version | Purpose |
|---|---|---|
| Room | 2.6.1 | Local SQLite database |
| Room KSP | companion | Annotation processing |
| KSP | 1.9.22-1.0.17 | Symbol processing for Room |

### Parsing
| Library | Version | Purpose |
|---|---|---|
| ML Kit Text Recognition | 16.0.1 | OCR for images |
| Jsoup | 1.17.2 | HTML parsing / URL scraping |

### Background Work
| Library | Version | Purpose |
|---|---|---|
| WorkManager | 2.9.0 | Background task scheduling |

### Build & Tooling
| Tool | Version | Purpose |
|---|---|---|
| Gradle | 8.4 | Build system |
| AGP (Android Gradle Plugin) | 8.2.2 | Android build plugin |
| Kotlin DSL | — | Gradle script language |
| Kotlin Gradle Plugin | 1.9.22 | Kotlin compilation |

### Testing
| Library | Version | Purpose |
|---|---|---|
| JUnit 5 (Jupiter) | 5.10.2 | Unit test framework |
| MockK | 1.13.10 | Kotlin mocking library |
| Turbine | 1.1.0 | Flow testing |
| Compose UI Test | via BOM | Compose UI testing |
| Room Testing | 2.6.1 | Database testing |
| Truth | 1.4.3 | Assertions |

### Other
| Library | Version | Purpose |
|---|---|---|
| Kotlinx Coroutines | 1.8.0 | Async programming |
| Kotlinx Serialization | 1.6.3 | JSON export |

## Environment

| Property | Value |
|---|---|
| IDE | Android Studio Hedgehog (2023.1.1+) |
| Package manager | Gradle (via `gradle-wrapper`) |
| Dependency file | `app/build.gradle.kts` + `libs.versions.toml` (Version Catalog) |
| Build command | `./gradlew assembleDebug` |
| Test command | `./gradlew testDebugUnitTest` |
| UI test command | `./gradlew connectedDebugAndroidTest` |
| Lint command | `./gradlew lintDebug` |
| Formatter | ktlint (via Spotless or manual) |

## Tools & Infrastructure

| Tool | Choice |
|---|---|
| CI/CD | GitHub Actions (to be set up) |
| Code quality | Detekt + ktlint |
| Version catalog | `gradle/libs.versions.toml` |
| APK signing | Manual (debug keystore) |
| ProGuard / R8 | Enabled for release builds |
| Containerization | Not applicable (native Android) |

## Versions Summary

| Tool / Library | Version |
|---|---|
| Kotlin | 1.9.22 |
| AGP | 8.2.2 |
| Gradle | 8.4 |
| Compose BOM | 2024.02.00 |
| Room | 2.6.1 |
| ML Kit OCR | 16.0.1 |
| Jsoup | 1.17.2 |
| WorkManager | 2.9.0 |
| Navigation Compose | 2.7.7 |
| Lifecycle | 2.7.0 |
| JUnit 5 | 5.10.2 |
| MockK | 1.13.10 |
| Turbine | 1.1.0 |
| Coroutines | 1.8.0 |
| Kotlinx Serialization | 1.6.3 |
