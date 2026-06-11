# Architecture — Job Tracker Android App

## Overview

The Job Tracker follows a **layered architecture** with unidirectional data flow. The app is built as a single-module Android project using Kotlin, Jetpack Compose, and Room. Communication flows from the system-level Share Intent through the parser engine, into the data layer, and up to the UI. Reminders are scheduled via WorkManager and sync with the system calendar.

## System Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                        ANDROID SYSTEM                               │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌───────────────────┐   │
│  │ WhatsApp  │  │ Telegram │  │   SMS    │  │   Browser (URL)   │   │
│  └─────┬────┘  └────┬─────┘  └────┬─────┘  └─────────┬─────────┘   │
│        │             │             │                  │             │
│        └─────────────┼─────────────┼──────────────────┘             │
│                      │             │                                │
│              ┌───────▼─────────────▼───────┐                        │
│              │   Share Intent Filter       │                        │
│              │   (ACTION_SEND)             │                        │
│              └───────┬─────────────────────┘                        │
└──────────────────────┼──────────────────────────────────────────────┘
                       │
              ┌────────▼────────┐
              │ ShareReceiver   │
              │  Activity       │
              │  (singleTop)    │
              └────────┬────────┘
                       │ raw text / URI
                       ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      PARSER ENGINE                                   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐               │
│  │ RegexParser  │  │  OcrParser   │  │ UrlScraper   │               │
│  │ (patterns)   │  │ (ML Kit)     │  │ (Jsoup)      │               │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘               │
│         │                 │                 │                        │
│         └─────────────────┼─────────────────┘                        │
│                           ▼                                          │
│                   ┌──────────────┐                                   │
│                   │ ParsedJob    │                                   │
│                   │ (data class) │                                   │
│                   └──────┬───────┘                                   │
└──────────────────────────┼───────────────────────────────────────────┘
                           │ parsed job
                           ▼
┌─────────────────────────────────────────────────────────────────────┐
│                        DATA LAYER                                    │
│  ┌─────────────────────┐    ┌────────────────────────────────────┐   │
│  │     Repository      │◄──►│         Room Database              │   │
│  │  JobRepository      │    │  ┌──────────┐  ┌───────────────┐  │   │
│  │  ReminderRepository │    │  │   JobDao  │  │  ReminderDao  │  │   │
│  └─────────┬───────────┘    │  ├──────────┤  ├───────────────┤  │   │
│            │                │  │   Job     │  │  Reminder     │  │   │
│            │                │  │  Entity   │  │  Entity       │  │   │
│            │                │  └──────────┘  └───────────────┘  │   │
│            │                └────────────────────────────────────┘   │
│            │                        ▲                                │
│            │                        │ Flow<List<Job>>                │
│            ▼                        │                                │
│  ┌──────────────────┐                                              │
│  │   ViewModels     │                                              │
│  └──────────────────┘                                              │
└──────────────────────────┬───────────────────────────────────────────┘
                           │ StateFlow<UiState>
                           ▼
┌─────────────────────────────────────────────────────────────────────┐
│                       UI LAYER (Compose)                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐               │
│  │   Dashboard  │  │   JobDetail  │  │   Filter/    │               │
│  │   Screen     │  │   Screen     │  │   Search     │               │
│  └──────────────┘  └──────────────┘  └──────────────┘               │
│  ┌──────────────┐  ┌──────────────┐                                 │
│  │   Settings   │  │   Onboarding │                                 │
│  │   Screen     │  │   Screen     │                                 │
│  └──────────────┘  └──────────────┘                                 │
│                                                                     │
│  ┌───────────────────────────────────────────────────────────┐      │
│  │              Liquid Glass Theme                            │      │
│  │  (AGSL shaders, glassmorphism, gradients, blur overlays)   │      │
│  └───────────────────────────────────────────────────────────┘      │
└─────────────────────────────────────────────────────────────────────┘

                ┌──────────────────────────────────────┐
                │         REMINDER SERVICE              │
                │  ┌────────────┐  ┌────────────────┐  │
                │  │ WorkManager│  │ CalendarContract│  │
                │  │  Worker    │──►  Sync           │  │
                │  └────────────┘  └────────────────┘  │
                └──────────────────────────────────────┘
```

## Data Flow

### 1. Incoming Share → Parse → Store → Display
1. User shares a message/link/image from any app → Android OS launches `ShareReceiverActivity`
2. `ShareReceiverActivity` extracts the content (text, URI, or both)
3. Content is dispatched to the **ParserEngine**:
   - **Text** → `RegexParser` applies curated regex patterns to extract company, role, salary, location, deadline
   - **Image** → `OcrParser` uses ML Kit Text Recognition to extract text → passes result to `RegexParser`
   - **URL** → `UrlScraper` fetches page via Jsoup → extracts title, meta, and structured data → passes to `RegexParser`
4. A `ParsedJob` data class is produced and shown to user for confirmation/edit
5. User confirms → saved via `JobRepository` → `JobDao.insert()` → Room persists
6. UI observes `Flow<List<Job>>` from repository → updates dashboard automatically

### 2. Reminder Flow
1. User sets reminder on a job (interview date, application deadline)
2. `ReminderRepository` saves to Room and enqueues `WorkManager` Worker
3. Worker triggers at specified time → creates CalendarContract event
4. Notification is shown with job details

## Key Decisions

| Decision | Choice | Rationale |
|---|---|---|
| Language | Kotlin 1.9+ | Modern, null-safe, Compose-first, industry standard for Android |
| UI Framework | Jetpack Compose BOM 2024.x | Declarative UI, less boilerplate, Liquid Glass effects achievable via Canvas/AGSL |
| Database | Room 2.6.x | Official Android ORM, compile-time SQL verification, Flow integration |
| DI | Manual / Hilt | Starting manual, can adopt Hilt later; keeps initial complexity low |
| Image Text | ML Kit Text Recognition v2 | On-device, no network required, supports latin + devanagari scripts |
| Web Scraping | Jsoup 1.17.x | Lightweight, well-maintained, excellent HTML parsing |
| Reminders | WorkManager + CalendarContract | WorkManager handles Doze/background restrictions; CalendarContract provides system-level visibility |
| Architecture | Single-module layered | Single module reduces complexity and build time; layers via packages |
| Shaders | AGSL (Android Graphics Shading Language) | Native support for custom fragment shaders in Compose, enables glass effects |
| State Management | ViewModel + StateFlow | Lifecycle-aware, testable, Compose-native |
| Testing | JUnit 5 + MockK + Turbine | JUnit 5 for modern test structure, MockK for Kotlin mocking, Turbine for Flow testing |

## Modules / Components

- **`app/`** — Main module, contains Application class, DI configuration, navigation
- **`data/`** — Database entities, DAOs, and repository implementations
  - `db/` — Room database class, entities (`JobEntity`, `ReminderEntity`), DAOs
  - `repository/` — `JobRepository`, `ReminderRepository`
- **`parser/`** — Parsing engine with multiple strategies
  - `RegexParser` — Pattern matching for plain text
  - `OcrParser` — ML Kit text recognition wrapper
  - `UrlScraper` — Jsoup-powered web scraper
  - `ParserOrchestrator` — Dispatches to correct parser based on input type
- **`receiver/`** — Share intent receiver activity
  - `ShareReceiverActivity` — Handles `ACTION_SEND`, routes to parser
- **`reminder/`** — Background reminder scheduling
  - `ReminderWorker` — WorkManager Worker
  - `CalendarSyncManager` — CalendarContract integration
- **`ui/`** — Jetpack Compose UI layer
  - `theme/` — Liquid Glass theme, colors, typography, shapes, AGSL shaders
  - `screens/` — Dashboard, JobDetail, FilterSearch, Settings, Onboarding screens
  - `components/` — Reusable composables (JobCard, StatCard, GlassPanel, etc.)
