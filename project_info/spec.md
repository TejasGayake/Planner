# Project Specification — Job Tracker Android App

## Overview

An Android application for tracking job opportunities received through messaging platforms (WhatsApp, Telegram, SMS) and other sources. The app intelligently parses incoming job details using regex patterns, OCR (ML Kit), and web scraping (Jsoup) — then stores them locally via Room database. Users can set calendar reminders for interviews/deadlines, view an interactive dashboard with statistics, and manage their job hunt through a beautiful Liquid Glass (iOS 26-inspired) Compose UI.

## Features

- [x] **Share Intent Receiver** — Catch job links/messages shared from WhatsApp, Telegram, SMS, email, and browsers
- [x] **Smart Parsing Engine** — Extract company, role, location, salary, deadline from raw text using regex + ML Kit OCR (images) + Jsoup (URL scraping)
- [x] **Local Database** — Room (SQLite) stores jobs, companies, reminders, and parse history
- [x] **Dashboard** — Overview with stats (total jobs, applied, interview, offer, rejected), recent entries, quick-add button
- [ ] **Calendar Reminders** — WorkManager-backed reminders that sync to CalendarContract for interviews and deadlines
- [ ] **Filter & Search** — Filter by company, status, date range; full-text search on role/company
- [ ] **Liquid Glass UI** — AGSL shader–powered glassmorphism effects, dynamic gradients, frosted panels
- [ ] **Dark Mode** — System-aware theme switching with custom Liquid Glass dark palette
- [ ] **Backup / Export** — JSON export of all jobs for external backup or analysis

## User Stories

### High Priority
- As a user, I want to **import a job posting** from WhatsApp by tapping "Share" so I don't have to manually copy details.
- As a user, I want the app to **auto-parse the company, role, and salary** from a shared message so I save time.
- As a user, I want to **view all my tracked jobs** on a dashboard so I can see my job hunt progress at a glance.
- As a user, I want to **set a reminder for an interview date** so I never miss an interview.
- As a user, I want to **update a job's status** (Applied → Interview → Offer → Rejected) so my pipeline stays accurate.

### Medium Priority
- As a user, I want to **search/filter jobs by company or keyword** so I can quickly find specific entries.
- As a user, I want to **scan a job poster image** from a WhatsApp photo so I can capture details from images.
- As a user, I want to **open a shared URL and auto-scrape** the job page so I get full details.

### Low Priority
- As a user, I want to **export my job data as JSON** so I can back it up or analyze elsewhere.
- As a user, I want a **widget on my home screen** showing my job hunt stats.
- As a user, I want **dark mode** that matches the Liquid Glass aesthetic.

## Requirements

### Functional

| ID | Requirement | Priority |
|---|---|---|
| F1 | App must register an intent filter for `ACTION_SEND` with `text/plain` | P0 |
| F2 | App must extract job details from plain text using regex patterns | P0 |
| F3 | App must extract job details from shared URLs via Jsoup scraping | P1 |
| F4 | App must extract text from shared images via ML Kit OCR | P1 |
| F5 | App must persist jobs in a Room SQLite database | P0 |
| F6 | App must display a dashboard with job statistics | P0 |
| F7 | App must support job CRUD operations (create, read, update, delete) | P0 |
| F8 | App must schedule reminders using WorkManager | P1 |
| F9 | App must sync reminders to CalendarContract | P1 |
| F10 | App must support filtering by status (Saved, Applied, Interview, Offer, Rejected) | P1 |
| F11 | App must support full-text search on role and company name | P1 |
| F12 | App must apply Liquid Glass visual theme consistently | P1 |

### Non-Functional

| ID | Requirement | Target |
|---|---|---|
| NF1 | Cold start time | < 2 seconds on mid-range device |
| NF2 | Database query time (list all jobs) | < 100ms for 500 records |
| NF3 | Parse time for shared text | < 500ms |
| NF4 | Parse time for shared URL | < 5 seconds |
| NF5 | App size | < 25 MB (excluding ML Kit models) |
| NF6 | Offline-first | Full functionality without internet |
| NF7 | Battery impact | Minimal (no background polling, only user-triggered work) |

## Scope

### In Scope
- Android app targeting API 34+ (Android 14+), min SDK 26
- Kotlin + Jetpack Compose UI
- Room database for local persistence
- Share intent receiver for text/URLs/images
- Regex + ML Kit OCR + Jsoup parser pipeline
- WorkManager + CalendarContract reminders
- Liquid Glass themed UI (glassmorphism, gradients, blur)
- Unit tests and basic UI tests

### Out of Scope
- iOS version
- Server-side sync or cloud backup
- Multi-user support
- Desktop or web version
- Built-in email client integration
- Auto-reply or application submission
