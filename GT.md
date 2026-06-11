# GT — Project Resume

```
Project:  Job Tracker Android App (Liquid Glass + iOS 26 UI)
Root:     G:\GT\planner
```

## How to Resume Later

### Step 1 — Open the project
```powershell
cd G:\GT\planner
```

### Step 2 — Read state
```powershell
# Quick summary (recommended)
.\resume.ps1

# Or read the files directly
Get-Content .\session_state.md -Head 30   # see last topic & next step
Get-Content .\PROGRESS.json | ConvertFrom-Json | Select-Object -ExpandProperty phases  # see build status
```

### Step 3 — Tell the AI
Paste this to any AI coding assistant:

> "Resume the Job Tracker Android App project from G:\GT\planner. Read session_state.md and info.md for full context. The spec is complete. Start Phase 1: generate the Android project scaffold (Kotlin + Jetpack Compose)."

## Resume Commands

### Interactive (PowerShell)
```powershell
.\resume.ps1
```

### Via opencode (AI)
Say: **"Run the resume command"** or **`/resume`**

This reads `PROGRESS.json`, finds pending phases, and launches the next one.

## State Files

| File | What it tells you |
|------|-------------------|
| `session_state.md` | Full context: last topic, decisions, next step |
| `PROGRESS.json` | Build phase status (pending/done/failed) |
| `info.md` | 1549-line master spec — everything about the app |

## Quick Context

- Spec: ✅ Complete (17 sections, 40+ features, 8 tiers)
- Code: ❌ Not started (all 6 build phases pending in PROGRESS.json)
- Last topic: iOS 26 Liquid Glass UI details fully integrated
- Next: Phase 1 — Generate Android project scaffold

## Phase Map (from PROGRESS.json)

```
1. Scaffold Project  → 2-5 in parallel → 6. Testing
    (scaffolder)        (data-layer,    (tester)
                         parser-engine,
                         ui-designer,
                         reminder-service)
```
