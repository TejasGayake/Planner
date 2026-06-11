# Project Session State — Ready to Resume

> Last updated: 2026-06-11
> Run `.\resume.ps1` in PowerShell to see this summary + open key files.

## Project: Job Tracker Android App

A Liquid Glass (iOS 26) styled Android app for tracking job opportunities from WhatsApp, Telegram, and other sources — with smart parsing, reminders, and calendar sync.

**DeepSeek conversation:** `link.txt` (https://chat.deepseek.com/share/wzvjr4rjbg1paj7h1o)

---

## Current Status

| Aspect | Status |
|--------|--------|
| Spec document | ✅ Complete (info.md — 1549 lines, 17 sections) |
| Visual design | ✅ Liquid Glass + iOS 26 fully spec'd (Section 4) |
| Data models | ✅ Room entities spec'd (Section 5) |
| Parser engine | ✅ Regex patterns + architecture spec'd (Section 6) |
| Feature roadmap | ✅ 8 tiers, 40+ features (Section 15) |
| Code generated | ❌ Not started yet |
| UI built | ❌ Not started yet |

---

## Key Files

| File | Purpose |
|------|---------|
| `project_info/info.md` | **Master spec** — everything in one file |
| `project_info/link.txt` | DeepSeek conversation URL |
| `project_info/conversation.txt` | Original DeepSeek share link |
| `project_info/conversation.md` | Full DeepSeek conversation (974 lines) |
| `project_info/ui_related_details.md` | Liquid Glass research + Android implementation details |
| `project_info/session_state.md` | This file — resume context |
| `resume.ps1` | One-click resume script |
| `PROGRESS.json` | Build phase tracker |
| `opencode.json` | AI build commands |
| `GT.md` in `project_info/` | Resume cheat sheet |

---

## Last Discussed Topic

**UI + Liquid Glass design details** — we finished integrating every minute detail from `ui_related_details.md` into `info.md` Section 4 (subsections 4.4.7–4.4.12). The full spec now includes:

- iOS 26 Material Stack (6 visual layers)
- Shape merging, identity tracking, morphing rules
- Apple's exact interactive physics (stiffness=180, damping=27)
- SwiftUI reference implementation (as Android target)
- AGSL shader code for refraction, Fresnel, chromatic dispersion
- AI prompt template for code generation
- 5 Android libraries for Liquid Glass

---

## Key Architectural Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Tech stack | Kotlin + Jetpack Compose | Native Android, best glass support |
| Database | Room (SQLite) | Local-first, no backend |
| Reminders | WorkManager + AlarmManager | Reliable, battery-friendly |
| Calendar | CalendarContract (no OAuth) | Free, no API key needed |
| Parsing | Regex + rule-based | Free, offline, 80% accuracy |
| OCR | ML Kit Text Recognition | Free, on-device |
| URL scraping | Jsoup | Free, respects robots.txt |
| Telegram | Bot API | Free, unlimited |
| Glass effect | AGSL shader (API 33+) + library fallback | Closest to iOS 26 quality |
| Spring physics | `spring(dampingRatio=1.0, stiffness=180)` | Apple's reference values |

---

## Build Plan (Recommended Order)

| Phase | Module | Ready to Start? |
|-------|--------|-----------------|
| 1 | **ParserEngine + tests** | ✅ Spec done — needs code |
| 2 | Data layer (Room entities + DAOs) | ✅ Spec done — needs code |
| 3 | ShareReceiverActivity | 🔲 ParserEngine must exist first |
| 4 | Reminder system (WorkManager) | 🔲 Data layer must exist first |
| 5 | Calendar integration | 🔲 |
| 6 | Compose UI (Dashboard + Edit Screen) | 🔲 |
| 7 | Telegram bot polling | 🔲 Optional |
| 8 | OCR integration | 🔲 Nice-to-have |

---

## Next Likely Steps

1. Generate the Android project scaffold (build.gradle, manifest, folder structure)
2. Create ParserEngine.kt with all regex patterns + test cases
3. Create Room entities + DAOs
4. Build ShareReceiverActivity
5. Build Compose UI screens with Liquid Glass theme
6. Wire everything together

---

## Quick Commands

```powershell
# View project file tree
Get-ChildItem -Recurse -Name

# Read key spec sections
Get-Content info.md -Head 10          # Start of spec
Select-String info.md -Pattern "^## "  # All sections

# Read session log
Select-String info.md -Pattern "^### 2026"  # All session entries
```
